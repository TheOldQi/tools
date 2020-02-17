package com.xiafei.tools;

import java.io.File;
import java.io.FileFilter;

/**
 * <P>Description: 将一个文件夹中大量文件分开存储，恢复文件夹可用. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:    </P>
 * <P>CREATE DATE: 2020/1/6 下午9:19</P>
 * <P>UPDATE DATE: </P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class RescueTooManyFiles {

    public static void main(String[] args) {
        rescue("/Users/qixiafei/meituan/offlineAsrTexts/29-30/1-8218", 10000);
    }


    public static void rescue(final String directoryPath, final int size) {

        File directory = new File(directoryPath);
        final File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return !pathname.isDirectory();
            }
        });
        final int length = files.length;
        int partition = length / size;
        if (length % size > 0) partition++;
        for (int i = 0, offset = 0; i < partition; i++) {
            final int len = Math.min(size, length - offset);
            final int max = offset + len;
            final String partitionDirectoryPath = directory + "/" + (offset + 1) + "-" + max + "/";
            File partitionDirectory = new File(partitionDirectoryPath);
            partitionDirectory.mkdir();
            for (; offset < max; offset++) {
                final File file = files[offset];
                file.renameTo(new File(partitionDirectoryPath + file.getName()));
            }
        }

    }
}
