package com.example.server.service.fundclass.impl;

import com.example.common.model.fundclass.vo.SecurityClassRow;
import com.example.common.model.fundclass.vo.SecurityClassTree;
import com.example.server.service.fundclass.FundClassTreeService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class FundClassTreeServiceImpl implements FundClassTreeService {
    private static final String mapKeyName = "className";
    private static final String mapKeyCode = "classCode";


    @Override
    public void readExcel() {

        DecimalFormat df2 = new DecimalFormat();
        df2.applyPattern("00");
        DecimalFormat df4 = new DecimalFormat();
        df4.applyPattern("0000");
        DecimalFormat df6 = new DecimalFormat();
        df6.applyPattern("000000");

        Map<String, SecurityClassTree> c1CodeMap = new HashMap<>();
        Map<String, SecurityClassTree> c1NameMap = new HashMap<>();
        try {
            //创建工作簿对象
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(
                    new FileInputStream("C:\\Users\\Administrator\\IdeaProjects\\mydemo\\server\\src\\main\\resources\\fundclass\\demo.xlsx"));
            //读取原始分类到内存
            XSSFSheet originClassCodeSheet = xssfWorkbook.getSheetAt(1);
            int maxRow = originClassCodeSheet.getLastRowNum();
            for (int row = 0; row <= maxRow; row++) {
                XSSFRow sheetRow = originClassCodeSheet.getRow(row);
                SecurityClassRow classRow = new SecurityClassRow(
                        getCellValue(sheetRow.getCell(0)),
                        getCellValue(sheetRow.getCell(1)),
                        getCellValue(sheetRow.getCell(2)),
                        getCellValue(sheetRow.getCell(3)),
                        getCellValue(sheetRow.getCell(4)),
                        getCellValue(sheetRow.getCell(5)));
                toClassTreeMap(c1CodeMap,mapKeyCode,classRow);
                toClassTreeMap(c1NameMap,mapKeyName,classRow);
            }

            //获取导入分类
            List<SecurityClassRow> indexMapRows = new ArrayList<>();
            XSSFSheet indexClassSheet = xssfWorkbook.getSheetAt(0);
            int lastRowNum = indexClassSheet.getLastRowNum();
            for (int rowNum = 0; rowNum <= lastRowNum; rowNum++) {
                XSSFRow sheetRow = indexClassSheet.getRow(rowNum);
                String inIndexCode = getCellValue(sheetRow.getCell(0));
                String inC1Name = getCellValue(sheetRow.getCell(1));
                String inC2Name = getCellValue(sheetRow.getCell(2));
                String inC3Name = getCellValue(sheetRow.getCell(3));

                SecurityClassTree c1Tree = null;
                Map<String, SecurityClassTree> c2NameMap = null;
                SecurityClassTree c2Tree = null;
                Map<String, SecurityClassTree> c3NameMap = null;
                SecurityClassTree c3Tree = null;
                // =====================c1===================================
                if (StringUtils.isNotBlank(inC1Name)) {
                    if (!c1NameMap.containsKey(inC1Name)){
                        //c1新增
                        Optional<SecurityClassTree> maxOptional = c1NameMap.values().stream()
                                .max(Comparator.comparing(SecurityClassTree::getClassCode));
                        String c1CodeMax = "0";
                        if (maxOptional.isPresent()){
                            c1CodeMax = maxOptional.get().getClassCode();
                        }
                        String c1CodeNext = df2.format(Integer.parseInt(c1CodeMax) + 1);
                        SecurityClassTree newC1Tree = new SecurityClassTree().setClassCode(c1CodeNext).setClassName(inC1Name);
                        c1NameMap.put(inC1Name,newC1Tree);
                        c1Tree = newC1Tree;
                    }else {
                        c1Tree = c1NameMap.get(inC1Name);
                    }
                    // ===================c2=========================================
                    if (StringUtils.isNotBlank(inC2Name)) {
                        c2NameMap = c1Tree.getSubClass();
                        if (!c2NameMap.containsKey(inC2Name)){
                            //c2新增
                            Optional<SecurityClassTree> maxOptional = c2NameMap.values().stream()
                                    .max(Comparator.comparing(SecurityClassTree::getClassCode));
                            String c2CodeMax = c1Tree.getClassCode() + "00";
                            if (maxOptional.isPresent()){
                                c2CodeMax = maxOptional.get().getClassCode().replaceFirst("0","");
                            }
                            String c2CodeNext = df4.format(Integer.parseInt(c2CodeMax) + 1);
                            SecurityClassTree newC2Tree = new SecurityClassTree().setClassCode(c2CodeNext).setClassName(inC2Name);
                            c2NameMap.put(inC2Name,newC2Tree);
                            c2Tree = newC2Tree;
                        }else {
                            c2Tree = c2NameMap.get(inC2Name);
                        }
                        // ======================c3=================================
                        if (StringUtils.isNotBlank(inC3Name)) {
                            c3NameMap = c2Tree.getSubClass();
                            if (!c3NameMap.containsKey(inC3Name)){
                                //c3新增
                                Optional<SecurityClassTree> maxOptional = c3NameMap.values().stream()
                                        .max(Comparator.comparing(SecurityClassTree::getClassCode));
                                String c3CodeMax = c2Tree.getClassCode() + "00";
                                if (maxOptional.isPresent()){
                                    c3CodeMax = maxOptional.get().getClassCode().replaceFirst("0","");
                                }
                                String c3CodeNext = df6.format(Integer.parseInt(c3CodeMax) + 1);
                                SecurityClassTree newC3Tree = new SecurityClassTree().setClassCode(c3CodeNext).setClassName(inC3Name);
                                c3NameMap.put(inC3Name,newC3Tree);
                                c3Tree = newC3Tree;
                            }else {
                                c3Tree = c3NameMap.get(inC3Name);
                            }
                        }
                    }
                }
                //整理指数
                SecurityClassRow indexMapRow = new SecurityClassRow()
                        .setIndexCode(inIndexCode);
                if (c1Tree!=null) {
                    indexMapRow.setC1Code(c1Tree.getClassCode());
                    indexMapRow.setC1Name(c1Tree.getClassName());
                }
                if (c2Tree != null) {
                    indexMapRow.setC2Code(c2Tree.getClassCode());
                    indexMapRow.setC2Name(c2Tree.getClassName());
                }
                if (c3Tree != null) {
                    indexMapRow.setC3Code(c3Tree.getClassCode());
                    indexMapRow.setC3Name(c3Tree.getClassName());
                }
                indexMapRows.add(indexMapRow);
            }
            List<SecurityClassRow> classMapRows = new ArrayList<>();
            for (Map.Entry<String, SecurityClassTree> c1NameTree : c1NameMap.entrySet()){
                String c1Name = c1NameTree.getKey();
                SecurityClassTree c1Tree = c1NameTree.getValue();

                SecurityClassRow classMapRow = new SecurityClassRow();
                classMapRow.setC1Code(c1Tree.getClassCode());
                classMapRow.setC1Name(c1Tree.getClassName());
                Map<String, SecurityClassTree> c2NameMap = c1Tree.getSubClass();
                if (MapUtils.isEmpty(c2NameMap)) {
                    classMapRows.add(classMapRow);
                    classMapRow = new SecurityClassRow();
                }else {
                    for (Map.Entry<String, SecurityClassTree> c2NameTree : c2NameMap.entrySet()){
                        String c2Name = c2NameTree.getKey();
                        SecurityClassTree c2Tree = c2NameTree.getValue();
                        classMapRow.setC2Code(c2Tree.getClassCode());
                        classMapRow.setC2Name(c2Tree.getClassName());
                        Map<String, SecurityClassTree> c3NameMap = c2Tree.getSubClass();
                        if (MapUtils.isEmpty(c3NameMap)) {
                            classMapRows.add(classMapRow);
                            classMapRow = classMapRow.copyC1();
                        }else {
                            for (Map.Entry<String, SecurityClassTree> c3NameTree : c3NameMap.entrySet()){
                                String c3Name = c3NameTree.getKey();
                                SecurityClassTree c3Tree = c3NameTree.getValue();
                                classMapRow.setC3Code(c3Tree.getClassCode());
                                classMapRow.setC3Name(c3Tree.getClassName());
                                classMapRows.add(classMapRow);
                                classMapRow = classMapRow.copyC2();
                            }
                        }
                    }
                }

            }

            System.out.println(c1CodeMap);
            System.out.println(c1NameMap);
            System.out.println(classMapRows);
            System.out.println(indexMapRows);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void toClassTreeMap(Map<String, SecurityClassTree> c1Map,String mapKey,SecurityClassRow classRow) {
        String c1Code = rmBk(classRow.getC1Code());
        String c1Name = rmBk(classRow.getC1Name());
        String c2Code = rmBk(classRow.getC2Code());
        String c2Name = rmBk(classRow.getC2Name());
        String c3Code = rmBk(classRow.getC3Code());
        String c3Name = rmBk(classRow.getC3Name());

        String c1MapKey = c1Code;
        String c2MapKey = c2Code;
        String c3MapKey = c3Code;
        if (mapKeyName.equals(mapKey)){
            c1MapKey = c1Name;
            c2MapKey = c2Name;
            c3MapKey = c3Name;
        }

        if (c1Map.containsKey(c1MapKey)){
            Map<String, SecurityClassTree> c2Map = c1Map.get(c1MapKey).getSubClass();
            if (c2Map.containsKey(c2MapKey)){
                Map<String, SecurityClassTree> c3Map = c2Map.get(c2MapKey).getSubClass();
                if (c3Map.containsKey(c3MapKey)){
                    System.out.println("警告，" + c1MapKey + "-"+ c2MapKey+ "下存在重复三级分类代码");
                }else {
                    if (StringUtils.isBlank(c3MapKey)) return;
                    SecurityClassTree c3Tree = new SecurityClassTree();
                    c3Tree.setClassCode(c3Code);
                    c3Tree.setClassName(c3Name);
                    c3Map.put(c3MapKey, c3Tree);
                }
            }else {
                if (StringUtils.isBlank(c2MapKey)) return;
                SecurityClassTree c2Tree = new SecurityClassTree();
                c2Tree.setClassCode(c2Code);
                c2Tree.setClassName(c2Name);
                c2Map.put(c2MapKey, c2Tree);

                if (StringUtils.isBlank(c3MapKey)) return;
                SecurityClassTree c3Tree = new SecurityClassTree();
                c3Tree.setClassCode(c3Code);
                c3Tree.setClassName(c3Name);
                c2Tree.getSubClass().put(c3MapKey, c3Tree);
            }
        }else {
            if (StringUtils.isBlank(c1MapKey)) return;
            SecurityClassTree c1Tree = new SecurityClassTree();
            c1Tree.setClassCode(c1Code);
            c1Tree.setClassName(c1Name);
            c1Map.put(c1MapKey, c1Tree);

            if (StringUtils.isBlank(c2MapKey)) return;
            SecurityClassTree c2Tree = new SecurityClassTree();
            c2Tree.setClassCode(c2Code);
            c2Tree.setClassName(c2Name);
            c1Tree.getSubClass().put(c2MapKey, c2Tree);

            if (StringUtils.isBlank(c3MapKey)) return;
            SecurityClassTree c3Tree = new SecurityClassTree();
            c3Tree.setClassCode(c3Code);
            c3Tree.setClassName(c3Name);
            c2Tree.getSubClass().put(c3MapKey, c3Tree);
        }
    }

    public String getCellValue(XSSFCell cell){
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        String cv = cell.getStringCellValue();
        if (StringUtils.isEmpty(cv)) return null;
        int start = cv.indexOf("（");
        if (start == -1) {
            start = cv.indexOf("(");
        }
        int end = cv.indexOf("）");
        if (end == -1) {
            end = cv.indexOf(")");
        }

        if (start != -1 && start < end) {
            cv = cv.substring(start +1,end);
        }
        return rmBk(cv);
    }

    public String rmBk(String str){
        if (str == null) return null;
        str = str.replaceAll("#N/A","");
        str = str.replaceAll("\n","");
        str = str.replaceAll("[()]","");
        return str.replaceAll("\\b","");
    }
}
