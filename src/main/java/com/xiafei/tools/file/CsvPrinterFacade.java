package com.xiafei.tools.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <P>Description: csv输出操作. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2020/1/15 下午5:02</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
@Slf4j
public class CsvPrinterFacade implements Closeable, Flushable {

    private final FileOutputStream fos;
    private final OutputStreamWriter osw;
    private final CSVPrinter csvPrinter;

    /**
     * 根据打印目标文件路径构建.
     *
     * @param filePath 打印目标文件路径
     * @return 此类实例
     * @throws IOException 文件流打开异常
     */
    public static CsvPrinterFacade of(final String filePath) throws IOException {

        return of(new File(filePath));
    }

    /**
     * 根据打印目标文件操作符构建.
     *
     * @param file 打印目标文件操作符
     * @return 此类实例
     * @throws IOException 文件流打开异常
     */
    public static CsvPrinterFacade of(final File file) throws IOException {
        final CSVFormat csvFormat = CSVFormat.DEFAULT;
        final FileOutputStream fos = new FileOutputStream(file, true);
        final OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        final CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
        return new CsvPrinterFacade(fos, osw, csvPrinter);
    }

    /**
     * 输出一行数据.
     *
     * @param oneLine 一行数据，列表每个元素是一列
     * @throws IOException 文件操作异常
     */
    public void println(List<?> oneLine) throws IOException {
        csvPrinter.printRecord(oneLine);
        csvPrinter.flush();
    }

    /**
     * 输出多行数据.
     *
     * @param multiLines 多行数据，列表每个元素是一行
     * @throws IOException 文件操作异常
     */
    public void printlns(List<List<?>> multiLines) throws IOException {
        csvPrinter.printRecords(multiLines);
        csvPrinter.flush();
    }

    /**
     * 安静的关闭所有流.
     */
    public void silenceClose() {
        if (csvPrinter != null) {
            try {
                csvPrinter.close();
            } catch (IOException e) {
                log.warn("关闭CSVPrinter异常", e);
            }
        }
        if (osw != null) {
            try {
                osw.close();
            } catch (IOException e) {
                log.warn("关闭OutputStreamWriter异常", e);
            }
        }
        if (fos != null) {
            try {
                fos.getFD().sync();
            } catch (IOException e) {
                log.warn("调用FileDescriptor同步写入异常", e);

            }

            try {

                fos.close();
            } catch (IOException e) {
                log.warn("关闭FileOutputStream异常", e);
            }
        }
    }

    private CsvPrinterFacade(final FileOutputStream fos, final OutputStreamWriter osw, final CSVPrinter csvPrinter)
            throws FileNotFoundException {
        this.fos = fos;
        this.osw = osw;
        this.csvPrinter = csvPrinter;

    }

    @Override
    public void close() throws IOException {
        silenceClose();
    }

    @Override
    public void flush() throws IOException {
        csvPrinter.flush();
    }
}

