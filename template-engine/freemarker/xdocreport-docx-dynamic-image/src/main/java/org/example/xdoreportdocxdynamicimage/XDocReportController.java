package org.example.xdoreportdocxdynamicimage;

import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class XDocReportController {

    @GetMapping("/generate-docx")
    public void generateDocx(HttpServletResponse response) throws IOException, XDocReportException {
        // 加载 DOCX 模板文件
        InputStream templateStream = new ClassPathResource("templates/template.docx").getInputStream();

        // 创建 XDocReport
        IXDocReport report = XDocReportRegistry.getRegistry().loadReport(templateStream, TemplateEngineKind.Freemarker);

        // 设置字段元数据，表明 "image" 字段是图片类型
        FieldsMetadata metadata = report.createFieldsMetadata();
        metadata.addFieldAsImage("image");

        // 准备数据模型
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");

        // 加载图片
        InputStream imageStream = new ClassPathResource("static/image.png").getInputStream();
        IImageProvider image = new ByteArrayImageProvider(imageStream);

        // 将图片添加到数据模型中
        data.put("image", image);

        // 创建上下文并填充数据
        IContext context = report.createContext();
        context.putMap(data);

        // 准备输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 使用 XDocReport 填充数据并生成最终的 DOCX 文档
        report.process(context, outputStream);

        // 设置响应头并将生成的 DOCX 文件输出到客户端
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=generated-doc.docx");
        response.getOutputStream().write(outputStream.toByteArray());
    }
}
