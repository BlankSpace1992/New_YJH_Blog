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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 文件处理工具类
 *
 * @author yujunhong
 * @date 2021/7/7 14:33
 */
@Slf4j
public class FileUtils {
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
        if (StringUtils.isNotEmpty(expandedName)) {
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
}
