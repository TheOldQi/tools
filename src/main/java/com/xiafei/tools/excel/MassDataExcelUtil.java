package com.xiafei.tools.excel;

/**
 * <P>Description: 海量数据Excel工具类. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE AT: 2019/1/4 12:03</P>
 * <P>UPDATE AT: 2019/1/4 12:03</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.8.0
 */
public class MassDataExcelUtil {
//
//    /**
//     * 文件导出
//     * @param params
//     * @param response
//     * @throws Exception
//     */
//    public void excelDownLoad(Map params, HttpServletResponse response)throws Exception {
//        int allRowNumbers = 0;
//        int rowMaxCount = 60000;
//        //查询记录数
//        allRowNumbers = appDao.queryCount("testCommonInfo.infoMangerForAllCount", params);
//
//        //是否大数据量(超过6W)
//        if(allRowNumbers > rowMaxCount){
//            List list = new ArrayList();
//            List downlist = new ArrayList();
//            //1.设置相应头
//            String filename = "导出TEST.zip";
//            filename = new String(filename.getBytes("GBK"), "iso-8859-1");
//            response.reset();
//            response.setContentType("application/octet-stream;charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment;filename="+filename);
//            response.addHeader("pargam", "no-cache");
//            response.addHeader("Cache-Control", "no-cache");
//
//            //2.设置批次文件名
//            String fileSuff = "";
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//            fileSuff = sdf.format(new Date());
//            String fileName = "导出TEST"+fileSuff;
//            List<String> fileNames = new ArrayList<String>();  //存放生成的文件名称
//            String filePath = "D:/excel/";   				   //上线后切换成linux服务器地址
//            if(!new File(filePath).exists()){
//                new File(filePath).mkdirs();
//            }
//            File zip = new File(filePath+fileName+".zip");  //压缩文件路径
//
//            //3.分批次生成excel
//            int  tempsize = (allRowNumbers%rowMaxCount)==0?allRowNumbers/rowMaxCount:allRowNumbers/rowMaxCount+1;
//            for (int i = 0; i < tempsize; i++) {
//                if(i == (allRowNumbers/rowMaxCount)){
//                    params.put("startNum", i*rowMaxCount);
//                    params.put("endNum", allRowNumbers);
//                }else{
//                    params.put("startNum", i*rowMaxCount);
//                    params.put("endNum", (i+1)*rowMaxCount);
//                }
//                list = appDao.queryCount("testCommonInfo.commonInfoForPC", params);
//
//                //3.2生成excel
//                String tempExcelFile = filePath+fileName+"["+(i+1)+"].xlsx";
//                fileNames.add(tempExcelFile);
//                FileOutputStream fos = new FileOutputStream(tempExcelFile);
//                int rowMemory = 100;
//                SXSSFWorkbook wb = new SXSSFWorkbook(rowMemory);
//                try{
//                    wb = exportDataToExcelXLSX(wb, downlist);
//                    wb.write(fos);
//                    fos.flush();
//                    fos.close();
//                }catch(RuntimeException runMsg){
//                    throw new Exception("查询数据信息异常 ");
//                }finally{
//                    fos.flush();
//                    fos.close();
//                    //手动清除list
//                    list.clear();
//                    downlist.clear();
//                }
//            }
//            //4.导出zip压缩文件
//            exportZip(response, fileNames, zip);
//
//    }
//
//
//    /**
//     * 文件压缩并导出到客户端
//     * @param outPut
//     * @param fileNames
//     * @param zip
//     * @throws FileNotFoundException
//     * @throws IOException
//     */
//    private void exportZip(HttpServletResponse response, List<String> fileNames, File zip)
//            throws FileNotFoundException, IOException {
//        OutputStream  outPut = response.getOutputStream();
//
//        //1.压缩文件
//        File srcFile[] = new File[fileNames.size()];
//        for (int i = 0; i < fileNames.size(); i++) {
//            srcFile[i] = new File(fileNames.get(i));
//        }
//        byte[] byt = new byte[1024];
//        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
//        out.setEncoding("UTF-8");
//        for (int i = 0; i < srcFile.length; i++) {
//            FileInputStream in = new FileInputStream(srcFile[i]);
//            out.putNextEntry(new ZipEntry(srcFile[i].getName()));
//            int length;
//            while((length=in.read(byt)) > 0){
//                out.write(byt,0,length);
//            }
//            out.closeEntry();
//            in.close();
//        }
//        out.close();
//
//        //2.删除服务器上的临时文件(excel)
//        for (int i = 0; i < srcFile.length; i++) {
//            File temFile = srcFile[i];
//            if(temFile.exists() && temFile.isFile()){
//                temFile.delete();
//            }
//        }
//
//        //3.返回客户端压缩文件
//        FileInputStream inStream = new FileInputStream(zip);
//        byte[] buf = new byte[4096];
//        int readLenght;
//        while((readLenght = inStream.read(buf)) != -1 ){
//            outPut.write(buf,0,readLenght);
//        }
//        inStream.close();
//        outPut.close();
//
//        //4.删除压缩文件
//        if(zip.exists() && zip.isFile()){
//            zip.delete();
//        }
//    }
//
//
//    /**
//     * 设置excel样式和数值
//     * @param wb
//     * @param listMap
//     * @param companyatr
//     * @return
//     */
//    private static SXSSFWorkbook exportDataToExcelXLSX(SXSSFWorkbook wb, List<Map> listMap){
//        String[] assetHeadTemp = {"系列","代码","名称","凭证号","状态","时间","数量"};
//        String[] assetNameTemp = {"TYPE","CODE","NAME","BILLNO","STATUS","TIME","NUM"};
//        Sheet sheet = null;
//        CellStyle columnHeadStyle = wb.createCellStyle();
//        columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);// 下边框
//        columnHeadStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
//        columnHeadStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
//        columnHeadStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
//        columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
//        columnHeadStyle.setWrapText(true);
//        Font f = wb.createFont();// 字体
//        f.setFontHeightInPoints((short) 9);// 字号
//        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//BOLDWEIGHT_BOLD);// 加粗
//        columnHeadStyle.setFont(f);
//        Row row;
//        Cell cell;
//        sheet = wb.createSheet("sheet");
//        row = sheet.createRow(0);
//        sheet.createFreezePane(0, 1, 0, 1);
//        for(int i=0;i<assetHeadTemp.length;i++){
//            cell = row.createCell(i);
//            cell.setCellStyle(columnHeadStyle);
//            cell.setCellValue(assetHeadTemp[i]);
//            sheet.setColumnWidth(i, (int)7000);
//        }
//        if(listMap != null && listMap.size() > 0){
//            int rowIndex = 1;
//            for(Map map : listMap){
//                row = sheet.createRow(rowIndex++);
//                int index = 0;
//                for(int i=0;i<assetNameTemp.length;i++ ){
//                    cell = row.createCell(index++);
//                    cell.setCellValue(map.get(assetNameTemp[i])!=null ?map.get(assetNameTemp[i]).toString():"");
//                }
//            }
//        }
//        return wb;
//    }
}
