package com.mountblue.mygoogledrive.controllers;

import com.mountblue.mygoogledrive.entities.Contact;
import com.mountblue.mygoogledrive.entities.File;
import com.mountblue.mygoogledrive.entities.User;
import com.mountblue.mygoogledrive.services.ContactService;
import com.mountblue.mygoogledrive.services.FileService;
import com.mountblue.mygoogledrive.services.ThumbnailService;
import com.mountblue.mygoogledrive.services.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class FileController {
    @Autowired
    private ThumbnailService thumbnailService;
    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;
    @Autowired
    private ContactService contactService;

    private Logger logger = LoggerFactory.getLogger(java.io.File.class);


    @GetMapping({"/","/drive", "/drive/my-drive"})
    public String home(Authentication authentication, Model model, @RequestParam(value = "fileId", defaultValue = " ", required = false) String fileId,
                       @RequestParam(value = "q", defaultValue = "", required = false) String q) {
        String name = authentication.getName();
        User user = userService.getUserByUsername(name);
        model.addAttribute("user", user);
        List<File> allFiles = fileService.getAllFiles(q, name);
        List<String> formattedSizes = new ArrayList<>();

        for (File file : allFiles) {
            formattedSizes.add(formatFileSize(file.getSize()));
        }
//        List<File> lastFourFiles = new ArrayList<>();
//        int startIndex = Math.max(0, allFiles.size() - 6);
//        for (int i = startIndex; i < allFiles.size(); i++) {
//            lastFourFiles.add(allFiles.get(i));
//        }
//
//        model.addAttribute("lastSixFiles", lastFourFiles);

        List<Contact> contacts = contactService.showContacts(authentication);
        model.addAttribute("contacts",contacts);
        model.addAttribute("fileId", fileId);
        model.addAttribute("allFiles", allFiles);
        model.addAttribute("formattedSizes", formattedSizes);
        return "home";
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + " KB";
        } else {
            return (size / (1024 * 1024)) + " MB";
        }
    }

    @GetMapping("/drive/trash")
    public String trash(Authentication authentication ,Model model, @RequestParam(value = "fileId", defaultValue = " ", required = false) String fileId) {
        String name = authentication.getName();
        User user = userService.getUserByUsername(name);
        model.addAttribute("user", user);
        List<File> allFiles = fileService.getTrashedFiles(name);
        List<String> formattedSizes = new ArrayList<>();

        for (File file : allFiles) {
            formattedSizes.add(formatFileSize(file.getSize()));
        }
        model.addAttribute("fileId", fileId);
        model.addAttribute("allFiles", allFiles);
        model.addAttribute("formattedSizes", formattedSizes);

        return "home";
    }

    @GetMapping("/drive/recent")
    public String recent(Authentication authentication ,Model model, @RequestParam(value = "fileId", defaultValue = " ", required = false) String fileId) {
        String name = authentication.getName();
        User user = userService.getUserByUsername(name);
        model.addAttribute("user", user);
        List<File> allFiles = fileService.getRecentFiles(name);
        List<String> formattedSizes = new ArrayList<>();

        for (File file : allFiles) {
            formattedSizes.add(formatFileSize(file.getSize()));
        }
        model.addAttribute("fileId", fileId);
        model.addAttribute("allFiles", allFiles);
        model.addAttribute("formattedSizes", formattedSizes);

        return "home";
    }

    @PostMapping("/drive/upload")
    public String fileUpload(@RequestParam("name") String name,@RequestParam("files[]") MultipartFile[] files, Model model) throws IOException {
        User user = userService.getUserByUsername(name);
        System.out.println(name);
        Arrays.stream(files).forEach(multipartFile -> {
            File uploadFile = new File();
            uploadFile.setUserName(name);
            uploadFile.setFileName(multipartFile.getOriginalFilename());
            System.out.println(multipartFile.getContentType());

            String fileType = extractFileType(multipartFile.getContentType());
            uploadFile.setFileType(fileType);

            try {
                uploadFile.setContent(multipartFile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            uploadFile.setSize(multipartFile.getSize());
            uploadFile.setUser(user);
            System.out.println(user);
            fileService.createFile(uploadFile);
        });
        model.addAttribute("success", "File Upload Successfully");
        return "redirect:/drive";
    }

    private String extractFileType(String contentType) {
        String[] parts = contentType.split("/");
        if (parts.length == 2) {
            return parts[1];
        }
        return "";
    }

    @GetMapping("/drive/view/{fileId}")
    @PreAuthorize("authentication.name == @fileService.getFileByFileId(#fileId).getUserName()")
    public ResponseEntity<Resource> viewFile(@PathVariable("fileId") Long fileId) {
        File file = fileService.findFileByFileId(fileId);
        String fileExtension = FilenameUtils.getExtension(file.getFileName());
        MediaType mediaType = MediaType.parseMediaType(getMimeTypeFromExtension(fileExtension));

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(file.getContent()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    private String getMimeTypeFromExtension(String extension) {
        if (extension.equalsIgnoreCase("png")) {
            return "image/png";
        }  else if (extension.equalsIgnoreCase("jpg")) {
            return "image/jpg";
        } else if (extension.equalsIgnoreCase("pdf")) {
            return "application/pdf";
        } else if (extension.equalsIgnoreCase("webm")) {
            return "video/webm";
        }
        else if (extension.equalsIgnoreCase("gif")) {
            return MimeTypeUtils.IMAGE_GIF_VALUE;
        }  else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
            return "application/msword";
        } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
            return "application/vnd.ms-excel";
        } else if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
            return "application/vnd.ms-powerpoint";
        } else if (extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("m4v") ||
                extension.equalsIgnoreCase("mov") || extension.equalsIgnoreCase("avi") ||
                extension.equalsIgnoreCase("mkv")) {
            return "video/" + extension;
        } else if (extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("htm")) {
            return "text/html";
        } else if (extension.equalsIgnoreCase("css")) {
            return "text/css";
        } else if (extension.equalsIgnoreCase("js")) {
            return "application/javascript";
        } else if (extension.equalsIgnoreCase("txt")) {
            return "text/plain";
        } else if (extension.equalsIgnoreCase("csv")) {
            return "text/csv";
        } else if (extension.equalsIgnoreCase("xml")) {
            return "application/xml";
        } else if (extension.equalsIgnoreCase("json")) {
            return "application/json";
        } else if (extension.equalsIgnoreCase("mp3")) {
            return "audio/mpeg";
        } else if (extension.equalsIgnoreCase("wav")) {
            return "audio/wav";
        } else if (extension.equalsIgnoreCase("zip")) {
            return "application/zip";
        } else {
            // Default MIME type for unknown file types
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }
    @GetMapping("/drive/pdfthumbnail/{fileId}")
    @PreAuthorize("authentication.name == @fileService.getFileByFileId(#fileId).getUserName()")
    public ResponseEntity<Resource> getPdfThumbnail(@PathVariable("fileId") Long fileId) throws IOException {
        File file = fileService.findFileByFileId(fileId);

        byte[] thumbnail = thumbnailService.generateThumbnail(file.getContent());
        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(thumbnail));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @GetMapping("/drive/preview{fileId}")
    @PreAuthorize("authentication.name == @fileService.getFileByFileId(#fileId).getUserName()")
    public void downloadFile(@PathVariable("fileId") Long fileId, Model model, HttpServletResponse response) throws IOException {
        File file = fileService.findFileByFileId(fileId);
        if (file != null) {
            response.setContentType("application.octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment: filename= " + file.getFileName();
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(file.getContent());
            outputStream.close();
        }
    }

    @GetMapping("/drive/download{fileId}")
    @PreAuthorize("authentication.name == @fileService.getFileByFileId(#fileId).getUserName()")
    public void showImage(@PathVariable("fileId") Long fileId, HttpServletResponse response, File file) throws IOException {
        file = fileService.findFileByFileId(fileId);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif, image/pdf");
        response.getOutputStream().write(file.getContent());
        response.getOutputStream().close();
    }

    @GetMapping("/drive/move{fileId}")
    @PreAuthorize("authentication.name == @fileService.getFileByFileId(#fileId).getUserName()")
    public String trashFile(@PathVariable("fileId") long fileId) {
        System.out.println("-----");
        fileService.move(fileId);
        return "redirect:/drive";
    }

    @GetMapping("/drive/delete{fileId}")
    @PreAuthorize("authentication.name == @fileService.getFileByFileId(#fileId).getUserName()")
    public String deleteFile(@PathVariable("fileId") long fileId) {
        fileService.delete(fileId);
        return "redirect:/drive";
    }

    @PostMapping("/drive/rename{fileId}")
    @PreAuthorize("authentication.name == @fileService.getFileByFileId(#fileId).getUserName()")
    public String renameFile(@PathVariable("fileId") long fileId, @RequestParam("name") String name) {
        fileService.renameFile(fileId, name);
        return "redirect:/drive";
    }

}
