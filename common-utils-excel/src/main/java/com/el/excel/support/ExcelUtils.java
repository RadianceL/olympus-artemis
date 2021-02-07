package com.el.excel.support;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.el.base.utils.collection.CollectionUtils;
import com.el.base.utils.support.io.local.LocalFileUtil;
import com.el.excel.annotation.ExcelStatement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * excel导出工具 <br/>
 * since 2020/8/10
 *
 * @author eddie.lys
 */
@Slf4j
public class ExcelUtils {

    private static final String CONTENT_TYPE = "application/vnd.ms-excel;charset=UTF-8";

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final String CONTENT_DISPOSITION_FILE = "attachment;filename=%s.xlsx";

    private static final String CACHE_CONTROL = "Cache-Control";

    private static final String CACHE_CONTROL_NO_STORE = "no-store";

    private static final String DEFAULT_EXCEL_NAME = "default";

    private static final String[] DEFAULT_SHEET_NAME = { "sheet1" };

    public static <T> List<T> readExcel(MultipartFile excelFileData, Class<T> clazz) {
        if (Objects.isNull(excelFileData) || Objects.isNull(clazz)) {
            throw new IllegalArgumentException("args can not be null! please check again ~");
        }
        try {
            return EasyExcel.read(excelFileData.getInputStream()).head(clazz).sheet().doReadSync();
        }catch (IOException e) {
            log.error("read excel exception!", e);
            return new ArrayList<>();
        }
    }

    public static <T> List<T> readExcel(InputStream excelFileData, Class<T> clazz) {
        if (Objects.isNull(excelFileData) || Objects.isNull(clazz)) {
            throw new IllegalArgumentException("args can not be null! please check again ~");
        }
        return EasyExcel.read(excelFileData).head(clazz).sheet().doReadSync();
    }

    public static <T> List<T> readExcel(String localPath, Class<T> clazz) {
        if (StringUtils.isNotBlank(localPath) || Objects.isNull(clazz)) {
            throw new IllegalArgumentException("args can not be null! please check again ~");
        }
        try {
            byte[] fileBytes = LocalFileUtil.readFileByBytes(localPath);
            if (Objects.isNull(fileBytes)) {
                log.error("read local excel file error: file bytes is empty");
                return new ArrayList<>();
            }
            return EasyExcel.read(new ByteArrayInputStream(fileBytes)).head(clazz).sheet().doReadSync();
        }catch (IOException e) {
            log.error("read excel exception!", e);
            return new ArrayList<>();
        }
    }

    public static <T> void exportExcel(HttpServletResponse httpServletResponse, Class<T> clazz, List<T> dataLists) {
        exportExcel(httpServletResponse, clazz, dataLists, declarationField -> declarationField);
    }

    public static <T> void exportExcel(HttpServletResponse httpServletResponse, Class<T> clazz, List<T> dataLists, MultilingualExtend multilingualExtend) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("data class can not be null! please check again ~");
        }

        if (Objects.isNull(dataLists)) {
            dataLists = new ArrayList<>();
        }

        String fileName = DEFAULT_EXCEL_NAME;
        String[] sheetNameArray = DEFAULT_SHEET_NAME;
        ExcelStatement annotation = clazz.getAnnotation(ExcelStatement.class);
        if (Objects.nonNull(annotation)) {
            if (StringUtils.isNotBlank(annotation.fileName())) {
                fileName = annotation.fileName();
            }

            if (CollectionUtils.isNotEmpty(annotation.sheet())) {
                sheetNameArray = annotation.sheet();
            }
        }
        try{
            configurationExcelHttpResponse(fileName, httpServletResponse);
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            ExcelWriterBuilder excelWriter = EasyExcel.write(outputStream, clazz);
            excelWriter.head(buildExcelExcelHead(clazz, multilingualExtend));
            excelWriter.sheet(0, sheetNameArray[0]).doWrite(dataLists);
        }catch (Throwable e) {
            log.error("export excel error happen on class [{}], fileName {}", clazz.getName(), fileName, e);
        }
    }

    private static List<List<String>> buildExcelExcelHead(Class<?> clazz, MultilingualExtend multilingualExtend) {
        List<List<String>> excelHeadList = new ArrayList<>();
        if (clazz.getDeclaredFields().length > 0) {
            for (Field declaredField : clazz.getDeclaredFields()) {
                ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                if (Objects.nonNull(annotation)) {
                    List<String> header = new ArrayList<>();
                    for (String excelHeadDescription : annotation.value()) {
                        header.add(multilingualExtend.buildExcelHeadName(excelHeadDescription));
                    }
                    excelHeadList.add(header);
                }
            }
        }
        return excelHeadList;
    }

    @SneakyThrows
    private static void configurationExcelHttpResponse(String fileName, HttpServletResponse response) {
        fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.displayName());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setHeader(CACHE_CONTROL, CACHE_CONTROL_NO_STORE);
        response.setHeader(CONTENT_DISPOSITION, String.format(CONTENT_DISPOSITION_FILE, fileName));
    }
}
