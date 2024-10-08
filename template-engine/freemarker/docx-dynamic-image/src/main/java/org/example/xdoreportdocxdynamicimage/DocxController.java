package org.example.xdoreportdocxdynamicimage;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DocxController {

    @Autowired
    private Configuration freemarkerConfig;

    @GetMapping("/generate-doc")
    public void generateDocx(HttpServletResponse response) throws Exception {
                // FreeMarker 模板数据
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");

        // 加载FreeMarker模板
        Template template = freemarkerConfig.getTemplate("template.ftl");
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);

        // 创建Word文档
        XWPFDocument document = new XWPFDocument();

        // 插入从FreeMarker生成的文本内容
        XWPFParagraph textParagraph = document.createParagraph();
        XWPFRun textRun = textParagraph.createRun();
        textRun.setText(content);

        // 插入图片
        ClassPathResource imageFile = new ClassPathResource("static/image.png");
        InputStream imageStream = imageFile.getInputStream();
        byte[] imageBytes = imageStream.readAllBytes();
        imageStream.close();

        XWPFParagraph imageParagraph = document.createParagraph();
        XWPFRun imageRun = imageParagraph.createRun();

        InputStream picInputStream = new ByteArrayInputStream(imageBytes);
        imageRun.addPicture(
            picInputStream,
            XWPFDocument.PICTURE_TYPE_PNG,
            "image.png",
            Units.toEMU(200),  // 图片宽度
            Units.toEMU(200)   // 图片高度
        );
        picInputStream.close();

        // 设置响应头，返回生成的Word文档
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=generated-doc.docx");

        // 输出Word文档
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        response.getOutputStream().write(outputStream.toByteArray());
    }
}
