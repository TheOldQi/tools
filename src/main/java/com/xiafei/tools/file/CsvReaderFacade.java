package com.xiafei.tools.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

/**
 * <P>Description: . </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2020/1/16 下午7:05</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class CsvReaderFacade implements Closeable {

    private final InputStream is;
    private final Reader r;
    private final CSVParser csvParser;

    /**
     * 根据打印目标文件路径构建.
     *
     * @param filePath 打印目标文件路径
     * @return 此类实例
     * @throws IOException 文件流打开异常
     */
    public static CsvReaderFacade of(final String filePath, final boolean skipHeader) throws IOException {

        return of(new File(filePath), skipHeader);
    }

    /**
     * 根据打印目标文件操作符构建.
     *
     * @param file 打印目标文件操作符
     * @return 此类实例
     * @throws IOException 文件流打开异常
     */
    public static CsvReaderFacade of(final File file, final boolean skipHeader) throws IOException {
        final CSVFormat csvFormat = CSVFormat.DEFAULT.withSkipHeaderRecord(skipHeader);
        final FileInputStream fis = new FileInputStream(file);
        final InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        final CSVParser csvParser = new CSVParser(isr, csvFormat);
        return new CsvReaderFacade(fis, isr, csvParser);
    }

    /**
     * 一次将所有记录加载到内存.
     *
     * @return 所有记录
     */
    public List<CSVRecord> readNormal() throws IOException {
        return csvParser.getRecords();
    }

    /**
     * 返回一个迭代器，每次调用next()才去请求文件，是流的方式，读取大文件时使用.
     *
     * @return 迭代器
     */
    public Iterator<CSVRecord> readLarge() {
        return csvParser.iterator();
    }


    /**
     * 安静的关闭所有流.
     */
    public void silenceClose() {
        if (csvParser != null) {
            try {
                csvParser.close();
            } catch (IOException e) {
                log.warn("关闭CSVParser异常", e);
            }
        }
        if (r != null) {
            try {
                r.close();
            } catch (IOException e) {
                log.warn("关闭Reader异常", e);
            }
        }
        if (is != null) {

            try {
                is.close();
            } catch (IOException e) {
                log.warn("关闭InputStream异常", e);
            }
        }
    }

    private CsvReaderFacade(final InputStream is, final Reader r, final CSVParser csvParser)
            throws FileNotFoundException {
        this.is = is;
        this.r = r;
        this.csvParser = csvParser;

    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        silenceClose();
    }

}
