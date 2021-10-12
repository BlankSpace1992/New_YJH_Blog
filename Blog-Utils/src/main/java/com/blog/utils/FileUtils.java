package com.blog.utils;

import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.emoji.EmojiImageType;
import com.vladsch.flexmark.ext.emoji.EmojiShortcutType;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 文件处理工具类
 *
 * @author yujunhong
 * @date 2021/7/7 14:33
 */
@Slf4j
public class FileUtils {
    public static final String[] IMG_FILE = {"bmp", "jpg", "png", "tif", "gif", "jpeg", "webp"};
    public static final String[] DOC_FILE = {"doc", "docx", "txt", "hlp", "wps", "rtf", "html", "pdf", "md", "sql",
            "css", "js", "vue", "java"};
    public static final String[] VIDEO_FILE = {"avi", "mp4", "mpg", "mov", "swf"};
    public static final String[] MUSIC_FILE = {"wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"};
    public static final String[] ALL_FILE = {"bmp", "jpg", "png", "tif", "gif", "jpeg", "webp",
            "doc", "docx", "txt", "hlp", "wps", "rtf", "html", "pdf", "md", "sql", "css", "js", "vue", "java",
            "avi", "mp4", "mpg", "mov", "swf",
            "wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac"
    };
    public static final int IMAGE_TYPE = 1;
    public static final int DOC_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int MUSIC_TYPE = 4;
    public static final int OTHER_TYPE = 5;
    /**
     * 图片类型
     */
    private static final List<String> PICTURE_TYPE_LIST = new ArrayList<>(Arrays.asList("jpg", "jpeg", "bmp", "gif",
            "png"));

    /**
     * 判断是否是图片
     *
     * @param fileName 文件名称
     * @return 是否为图片
     * @author yujunhong
     * @date 2021/7/7 14:36
     */
    public static boolean isPicture(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        String expandedName = getExpandedName(fileName);
        return PICTURE_TYPE_LIST.contains(expandedName);
    }

    /**
     * 获取后缀名
     *
     * @param fileName 文件名称
     * @return 文件后缀名
     * @author yujunhong
     * @date 2021/7/7 14:38
     */
    public static String getExpandedName(String fileName) {
        String expandedName = StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(fileName) && StringUtils.contains(fileName, ".")) {
            expandedName = StringUtils.substring(fileName, fileName.indexOf(".") + 1);
        }
        expandedName = expandedName.toLowerCase();
        if (StringUtils.isEmpty(expandedName)) {
            return "jpg";
        }
        return expandedName;
    }

    /**
     * 判断是否为markdown文档
     *
     * @param fileName 文件名称
     * @return 是否为markdown文档
     * @author yujunhong
     * @date 2021/7/7 14:46
     */
    public static boolean isMarkdown(String fileName) {
        return fileName.contains(".md");
    }

    /**
     * 讲markdown文档转换为html文件
     *
     * @param markdown markdown文件内容
     * @return html格式内容
     * @author yujunhong
     * @date 2021/7/7 14:49
     */
    public static String markdownToHtml(String markdown) {
        MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                AutolinkExtension.create(),
                EmojiExtension.create(),
                StrikethroughExtension.create(),
                TaskListExtension.create(),
                TablesExtension.create()
        ))
                // set GitHub table parsing options
                .set(TablesExtension.WITH_CAPTION, false)
                .set(TablesExtension.COLUMN_SPANS, false)
                .set(TablesExtension.MIN_HEADER_ROWS, 1)
                .set(TablesExtension.MAX_HEADER_ROWS, 1)
                .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
                // 设置gihub表情
                .set(EmojiExtension.USE_SHORTCUT_TYPE, EmojiShortcutType.GITHUB)
                .set(EmojiExtension.USE_IMAGE_TYPE, EmojiImageType.IMAGE_ONLY);
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    /**
     * html转markdown
     *
     * @param html html格式内容
     * @return markdown格式内容
     * @author yujunhong
     * @date 2021/7/7 14:59
     */
    public static String htmlToMarkdown(String html) {
        MutableDataSet options = new MutableDataSet();
        return FlexmarkHtmlConverter.builder(options).build().convert(html);
    }

    /**
     * 上传文件
     *
     * @param pathRoot      webapp所在路径
     * @param baseUrl       基础路径
     * @param multipartFile 文件
     * @return 上传路径
     * @author yujunhong
     * @date 2021/7/7 15:03
     */
    public static String uploadFile(String pathRoot, String baseUrl, MultipartFile multipartFile) throws IOException {
        String path = StringUtils.EMPTY;
        if (!multipartFile.isEmpty()) {
            // 获取文件类型
            String contentType = Optional.ofNullable(multipartFile.getContentType()).orElse(StringUtils.EMPTY);
            // 是否包含image
            if (contentType.contains("image")) {
                //获得文件后缀名称
                String imageName = contentType.substring(contentType.indexOf("/") + 1);
                path = baseUrl + StringUtils.getUUID() + "." + imageName;
                multipartFile.transferTo(new File(pathRoot + path));
            }
        }
        return path;
    }

    /**
     * 根据文件全名称(包含后缀)获取文件名称
     *
     * @param originalFileName 全名称(包含后缀名)
     * @return 文件名称
     * @author yujunhong
     * @date 2021/7/7 15:23
     */
    public static String getFileName(String originalFileName) {
        String fileName = StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(originalFileName) && originalFileName.contains(".")) {
            fileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * 从request中获取文件
     *
     * @param request 请求
     * @return 文件集合
     * @author yujunhong
     * @date 2021/7/7 15:25
     */
    public static List<MultipartFile> getMultipartFiles(HttpServletRequest request) {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        try {
            CommonsMultipartResolver commonsMultipartResolver =
                    new CommonsMultipartResolver(request.getSession().getServletContext());
            // 判断请求是否属于MultipartHttpServletRequest
            if (request instanceof MultipartHttpServletRequest) {
                // 将request变成多部分request
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                // 获取请求中包含的文件
                Iterator<String> fileNames = multiRequest.getFileNames();
                // 检查form中是否有enctype="multipart/form-data"
                if (commonsMultipartResolver.isMultipart(multiRequest) && fileNames.hasNext()) {
                    // 获取multiRequest 中所有的文件名
                    while (fileNames.hasNext()) {
                        List<MultipartFile> fileRows = multiRequest.getFiles(fileNames.next());
                        if (StringUtils.isNotEmpty(fileRows)) {
                            for (MultipartFile file : fileRows) {
                                if (file != null && !file.isEmpty()) {
                                    multipartFiles.add(file);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析MultipartRequest错误", e);
        }
        return multipartFiles;
    }

    public static List<String> getFileExtendsByType(int fileType) {
        List<String> fileExtends;
        switch (fileType) {
            case IMAGE_TYPE:
                fileExtends = Arrays.asList(IMG_FILE);
                break;

            case DOC_TYPE:
                fileExtends = Arrays.asList(DOC_FILE);
                break;
            case VIDEO_TYPE:
                fileExtends = Arrays.asList(VIDEO_FILE);
                break;
            case MUSIC_TYPE:
                fileExtends = Arrays.asList(MUSIC_FILE);
                break;
            case OTHER_TYPE: {
                fileExtends = Arrays.asList(ALL_FILE);
            }
            break;
            default:
                fileExtends = new ArrayList<>();
                break;


        }
        return fileExtends;
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            log.error("删除文件失败: {} 不存在！", fileName);
            return false;
        } else {
            if (file.isFile()) {
                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                log.info("删除单个文件：{} 成功！", fileName);
                return true;
            } else {
                log.error("删除单个文件：{} 失败！", fileName);
                return false;
            }
        } else {
            log.error("删除单个文件失败：{} 不存在！", fileName);
            return false;
        }
    }

    /**
     * 批量删除本地文件
     *
     * @param fileNameList 文件名称
     * @return 是否删除成功
     * @author yujunhong
     * @date 2021/10/11 14:30
     */
    public static boolean deleteFileList(List<String> fileNameList) {
        int successCount = 0;
        for (String fileName : fileNameList) {
            File file = new File(fileName);
            // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    log.info("删除单个文件：{} 成功！", fileName);
                    successCount += 1;
                } else {
                    log.error("删除单个文件：{} 失败！", fileName);
                }
            } else {
                log.error("删除单个文件失败：{} 不存在！", fileName);
            }
        }
        if (successCount == fileNameList.size()) {
            log.info("所有文件删除成功！");
            return true;
        } else {
            log.error("存在删除失败的文件！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     * @author yujunhong
     * @date 2021/10/11 14:30
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            log.error("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            log.error("删除目录 {} 成功！", dir);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将File转换成MultipartFile
     *
     * @param file 文件
     * @return MultipartFile
     * @author yujunhong
     * @date 2021/10/11 14:29
     */
    public static MultipartFile fileToMultipartFile(File file) {
        InputStream inputStream = null;
        MultipartFile multipartFile = null;
        try {
            inputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile(file.getName(), inputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    log.error(ioException.getMessage());
                }
            }
        }
        return multipartFile;
    }
}
