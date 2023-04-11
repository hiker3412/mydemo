package com.example.server.service.fundclass.impl;

import com.example.common.model.fundclass.vo.SecurityClassRow;
import com.example.common.model.fundclass.vo.SecurityClassTree;
import com.example.server.service.fundclass.FundClassTreeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class FundClassTreeServiceImpl implements FundClassTreeService {
    private static final String mapKeyName = "className";
    private static final String mapKeyCode = "classCode";


    public void readExcel() {
        int c1i = 0;
        int c2i = 0;
        int c3i = 0;

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("00");

        Map<String,String> class1Map = new HashMap<>();
        Map<String,String> class2Map = new HashMap<>();
        Map<String,String> class3Map = new HashMap<>();

        Map<String, SecurityClassTree> c1CodeMap = new HashMap<>();
        Map<String, SecurityClassTree> c1NameMap = new HashMap<>();
        try {
            //创建工作簿对象
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(
                    new FileInputStream("C:\\Users\\Administrator\\IdeaProjects\\mydemo\\src\\test\\resources\\demo.xlsx"));
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
            System.out.println(c1CodeMap);
            System.out.println(c1NameMap);

            //获取导入分类
            XSSFSheet indexClassSheet = xssfWorkbook.getSheetAt(0);
            int lastRowNum = indexClassSheet.getLastRowNum();
            for (int rowNum = 0; rowNum <= lastRowNum; rowNum++) {
                XSSFRow sheetRow = indexClassSheet.getRow(rowNum);
                String indexCode = getCellValue(sheetRow.getCell(0));
                String inC1Name = getCellValue(sheetRow.getCell(1));
                String inC2Name = getCellValue(sheetRow.getCell(2));
                String inC3Name = getCellValue(sheetRow.getCell(3));

//                //c1重命名
//                if (StringUtils.isNotBlank(inC2Name)) {
//                    String[] c2Rename = inC2Name.split("[（）]");
//                    if (c2Rename.length > 1) {
//                        Map<String, SecurityClassTree> c2Map = c1NameMap.get(inC1Name).getSubClass();
//                        SecurityClassTree c2Tree = c2Map.get(inC2Name);
//                        c2Tree.setClassName(c2Rename[1]);
//                        c3Map.put(c3Rename[1], c3Tree);
//                        c3Map.remove(c3Rename[0]);
//                    }
//                }
//
//                //c2重命名
//                if (StringUtils.isNotBlank(inC2Name)) {
//                    String[] c2Rename = inC2Name.split("[（）]");
//                    if (c2Rename.length > 1) {
//                        Map<String, SecurityClassTree> c2Map = c1NameMap.get(inC1Name).getSubClass();
//                        SecurityClassTree c2Tree = c2Map.get(inC2Name);
//                        c2Tree.setClassName(c2Rename[1]);
//                        c3Map.put(c3Rename[1], c3Tree);
//                        c3Map.remove(c3Rename[0]);
//                    }
//                }
//
//                //c3重命名
//                if (StringUtils.isNotBlank(inC3Name)) {
//                    String[] c3Rename = inC3Name.split("[（）]");
//                    if (c3Rename.length > 1) {
//                        SecurityClassTree c2Tree = c1NameMap.get(inC1Name).getSubClass().get(inC2Name);
//                        Map<String, SecurityClassTree> c3Map = c2Tree.getSubClass();
//                        SecurityClassTree c3Tree = c3Map.get(c3Rename[0]);
//                        c3Tree.setClassName(c3Rename[1]);
//                        c3Map.put(c3Rename[1], c3Tree);
//                        c3Map.remove(c3Rename[0]);
//                    }
//                }


            }

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
        return cell.getStringCellValue();
    }

    public String rmBk(String str){
        if (str == null) return null;
        return str.replaceAll("\\b","");
    }
}
