package com.example.server.service.fundclass.impl;

import com.example.common.model.fundclass.vo.ClassRow;
import com.example.common.model.fundclass.vo.ClassTree;
import com.example.common.model.fundclass.vo.SecurityClassRow;
import com.example.common.util.StringUtils;
import com.example.server.service.fundclass.FundClassTreeService;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

import static com.example.common.util.NumberFmt.*;

@Service
public class FundClassTreeServiceImpl implements FundClassTreeService {
    private static final String mapKeyName = "className";
    private static final String mapKeyCode = "classCode";
    public static String customIndexClass1 = "customIndexClass1";
    public static String customIndexClass2 = "customIndexClass2";
    public static String customIndexClass3 = "customIndexClass3";
    public static String windIndexCodeToCustomIndexClass = "windIndexCodeToCustomIndexClass";


    @Override
    public void updateSecurityClass() throws Exception {
        //连接数据源
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(
                new FileInputStream("C:\\Users\\Administrator\\IdeaProjects\\mydemo\\server\\src\\main\\resources\\fundclass\\demo.xlsx"));
        //读取原有分类到list
        XSSFSheet originClassCodeSheet = xssfWorkbook.getSheetAt(1);
        int maxRow = originClassCodeSheet.getLastRowNum();
        List<ClassRow> originClassRows = new ArrayList<>();
        for (int row = 0; row <= maxRow; row++) {
            XSSFRow sheetRow = originClassCodeSheet.getRow(row);
            SecurityClassRow classRow = new SecurityClassRow(
                    getCellValue(sheetRow.getCell(0)),
                    getCellValue(sheetRow.getCell(1)),
                    getCellValue(sheetRow.getCell(2)),
                    getCellValue(sheetRow.getCell(3)),
                    getCellValue(sheetRow.getCell(4)),
                    getCellValue(sheetRow.getCell(5)));
            originClassRows.add(classRow);
        }
        //原有分类行转成map树
        Map<String, ClassTree> classNameMap = classRowsToClassMap(originClassRows, mapKeyName);
        //读取新的指数分类行
        List<SecurityClassRow> securityClassRows = new ArrayList<>();
        XSSFSheet indexClassSheet = xssfWorkbook.getSheetAt(0);
        int lastRowNum = indexClassSheet.getLastRowNum();
        for (int rowNum = 0; rowNum <= lastRowNum; rowNum++) {
            XSSFRow sheetRow = indexClassSheet.getRow(rowNum);
            String inIndexCode = getCellValue(sheetRow.getCell(0));
            String inC1Name = getCellValue(sheetRow.getCell(1));
            String inC2Name = getCellValue(sheetRow.getCell(2));
            String inC3Name = getCellValue(sheetRow.getCell(3));
            SecurityClassRow classNameRow = (SecurityClassRow) new SecurityClassRow()
                    .setIndexCode(inIndexCode)
                    .setC1Name(inC1Name)
                    .setC2Name(inC2Name)
                    .setC3Name(inC3Name);
            securityClassRows.add(classNameRow);
        }
        //原有分类树和新的指数分类行相互更新
        cMapAndCRowsMutualUpdate(classNameMap, securityClassRows);
        List<ClassRow> classRows = classMapToClassRows(classNameMap);

        //入库
        String sql = generateInsertSql(classRows, securityClassRows);
        FileWriter fw = new FileWriter("C:\\Users\\Administrator\\IdeaProjects\\mydemo\\server\\src\\main\\resources\\fundclass\\classSql.sql");
        fw.write("");
        fw.write(sql);
        fw.flush();
        fw.close();
    }

    /**
     * 生成sql
     */
    private String generateInsertSql(List<ClassRow> classRows, List<SecurityClassRow> securityClassRows) {
        StringBuilder sb = new StringBuilder();
        sb.append("--删除指数分类\n");
        sb.append("delete from T_DATA_DICT where GROUP_ID = '" + customIndexClass1 + "'").append("\n/\n");
        sb.append("delete from T_DATA_DICT where GROUP_ID = '" + customIndexClass2 + "'").append("\n/\n");
        sb.append("delete from T_DATA_DICT where GROUP_ID = '" + customIndexClass3 + "'").append("\n/\n");
        sb.append("--删除指数分类映射\n");
        sb.append("delete from T_DATA_DICT where GROUP_ID = '" + windIndexCodeToCustomIndexClass + "'").append("\n/\n");
        sb.append("--插入新的指数分类\n");
        Set<String> c1Codes = new HashSet<>();
        Set<String> c2Codes = new HashSet<>();
        Set<String> c3Codes = new HashSet<>();
        for (ClassRow classRow : classRows) {
            if (StringUtils.isBlank(classRow.getC1Code())) {
                continue;
            }
            if (!c1Codes.contains(classRow.getC1Code())) {
                c1Codes.add(classRow.getC1Code());
                sb.append("insert into T_DATA_DICT(GROUP_ID,K,V) VALUES('" + customIndexClass1 + "','" + classRow.getC1Code() + "','" + classRow.getC1Name() + "')").append("\n/\n");

            }

            if (StringUtils.isBlank(classRow.getC2Code())) {
                continue;
            }
            if (!c2Codes.contains(classRow.getC2Code())) {
                String c2Code = classRow.getC2Code();
                String c2Name = classRow.getC2Name();
                if ("其他".equals(c2Name.trim())) {
                    c2Name = c2Name + "-" + classRow.getC1Name();
                }
                sb.append("insert into T_DATA_DICT(GROUP_ID,K,V) VALUES('" + customIndexClass2 + "','" + c2Code + "','" + c2Name + "')").append("\n/\n");
                c2Codes.add(c2Code);
            }

            if (StringUtils.isBlank(classRow.getC3Code())) {
                continue;
            }
            if (!c3Codes.contains(classRow.getC3Code())) {
                String c3Code = classRow.getC3Code();
                String c3Name = classRow.getC3Name();
                if ("其他".equals(c3Name.trim())) {
                    c3Name = c3Name + "-" + classRow.getC1Name();
                }
                sb.append("insert into T_DATA_DICT(GROUP_ID,K,V) VALUES('" + customIndexClass3 + "','" + c3Code + "','" + c3Name + "')").append("\n/\n");
                c3Codes.add(c3Code);
            }
        }
        sb.append("--插入新的指数分类映射\n");
        Set<String> indexCodes = new HashSet<>();
        for (SecurityClassRow row : securityClassRows) {
            if (StringUtils.isBlank(row.getIndexCode()) || StringUtils.isBlank(row.getC1Code())) {
                continue;
            }
            if (indexCodes.contains(row.getIndexCode())) continue;
            indexCodes.add(row.getIndexCode());
            if (StringUtils.isNotBlank(row.getC3Code())) {
                sb.append("insert into T_DATA_DICT(GROUP_ID,K,V) VALUES('" + windIndexCodeToCustomIndexClass + "','" + row.getIndexCode() + "','" + row.getC3Code() + "')").append("\n/\n");
            } else if (StringUtils.isNotBlank(row.getC2Code())) {
                sb.append("insert into T_DATA_DICT(GROUP_ID,K,V) VALUES('" + windIndexCodeToCustomIndexClass + "','" + row.getIndexCode() + "','" + row.getC2Code() + "')").append("\n/\n");
            } else {
                sb.append("insert into T_DATA_DICT(GROUP_ID,K,V) VALUES('" + windIndexCodeToCustomIndexClass + "','" + row.getIndexCode() + "','" + row.getC1Code() + "')").append("\n/\n");
            }
        }
        return sb.toString();
    }

    /**
     * 分类map树和指数分类记录相互更新
     * - 从指数分类名称更新原有分类名称(新增)
     * - 从原有分类更新指数分类code
     *
     * @param c1NameMap             原有的分类map树，k-指数分类名称，v-指数分类对象
     * @param securityClassNameRows 指数分类记录，每个指数对应的各级分类名称
     */
    private void cMapAndCRowsMutualUpdate(Map<String, ClassTree> c1NameMap, List<SecurityClassRow> securityClassNameRows) {
        for (SecurityClassRow securityClassRow : securityClassNameRows) {
            String c1Name = securityClassRow.getC1Name();
            String c2Name = securityClassRow.getC2Name();
            String c3Name = securityClassRow.getC3Name();

            ClassTree c1Tree = null;
            Map<String, ClassTree> c2NameMap = null;
            ClassTree c2Tree = null;
            Map<String, ClassTree> c3NameMap = null;
            ClassTree c3Tree = null;
            //===================更新原有分类===============================
            // =====================c1===================================
            if (StringUtils.isNotBlank(c1Name)) {
                if (c1NameMap.containsKey(c1Name)) {
                    c1Tree = c1NameMap.get(c1Name);
                } else {
                    //c1新增
                    Optional<ClassTree> maxOptional = c1NameMap.values().stream()
                            .max(Comparator.comparing(ClassTree::getClassCode));
                    String c1CodeMax = "0";
                    if (maxOptional.isPresent()) {
                        c1CodeMax = maxOptional.get().getClassCode();
                    }
                    String c1CodeNext = int2.format(Integer.parseInt(c1CodeMax) + 1);
                    ClassTree newC1Tree = new ClassTree().setClassCode(c1CodeNext).setClassName(c1Name);
                    c1NameMap.put(c1Name, newC1Tree);
                    c1Tree = newC1Tree;
                }
                // ===================c2=========================================
                if (StringUtils.isNotBlank(c2Name)) {
                    c2NameMap = c1Tree.getSubClass();
                    if (c2NameMap.containsKey(c2Name)) {
                        c2Tree = c2NameMap.get(c2Name);
                    } else {
                        //c2新增
                        Optional<ClassTree> maxOptional = c2NameMap.values().stream()
                                .max(Comparator.comparing(ClassTree::getClassCode));
                        String c2CodeMax = c1Tree.getClassCode() + "00";
                        if (maxOptional.isPresent()) {
                            c2CodeMax = maxOptional.get().getClassCode().replaceFirst("0", "");
                        }
                        String c2CodeNext = int4.format(Integer.parseInt(c2CodeMax) + 1);
                        ClassTree newC2Tree = new ClassTree().setClassCode(c2CodeNext).setClassName(c2Name);
                        c2NameMap.put(c2Name, newC2Tree);
                        c2Tree = newC2Tree;
                    }
                    // ======================c3=================================
                    if (StringUtils.isNotBlank(c3Name)) {
                        c3NameMap = c2Tree.getSubClass();
                        if (c3NameMap.containsKey(c3Name)) {
                            c3Tree = c3NameMap.get(c3Name);
                        } else {
                            //c3新增
                            Optional<ClassTree> maxOptional = c3NameMap.values().stream()
                                    .max(Comparator.comparing(ClassTree::getClassCode));
                            String c3CodeMax = c2Tree.getClassCode() + "00";
                            if (maxOptional.isPresent()) {
                                c3CodeMax = maxOptional.get().getClassCode().replaceFirst("0", "");
                            }
                            String c3CodeNext = int6.format(Integer.parseInt(c3CodeMax) + 1);
                            ClassTree newC3Tree = new ClassTree().setClassCode(c3CodeNext).setClassName(c3Name);
                            c3NameMap.put(c3Name, newC3Tree);
                            c3Tree = newC3Tree;
                        }
                    }
                }
            }

            //更新指数分类code
            if (c1Tree != null) {
                securityClassRow.setC1Code(c1Tree.getClassCode());
            }
            if (c2Tree != null) {
                securityClassRow.setC2Code(c2Tree.getClassCode());
            }
            if (c3Tree != null) {
                securityClassRow.setC3Code(c3Tree.getClassCode());
            }
        }
    }

    /**
     * 将逐条分类行转换成map分类树的结构
     *
     * @param classRows  各级一一对应的分类行
     * @param mapKeyType map分类树的key，分类code或分类name
     * @return map分类树
     */
    private Map<String, ClassTree> classRowsToClassMap(List<ClassRow> classRows, String mapKeyType) {
        Map<String, ClassTree> c1Map = new HashMap<>();
        for (ClassRow classRow : classRows) {
            String c1Code = rmBk(classRow.getC1Code());
            String c1Name = rmBk(classRow.getC1Name());
            String c2Code = rmBk(classRow.getC2Code());
            String c2Name = rmBk(classRow.getC2Name());
            String c3Code = rmBk(classRow.getC3Code());
            String c3Name = rmBk(classRow.getC3Name());

            String c1MapKey = c1Code;
            String c2MapKey = c2Code;
            String c3MapKey = c3Code;
            if (mapKeyName.equals(mapKeyType)) {
                c1MapKey = c1Name;
                c2MapKey = c2Name;
                c3MapKey = c3Name;
            }

            if (c1Map.containsKey(c1MapKey)) {
                Map<String, ClassTree> c2Map = c1Map.get(c1MapKey).getSubClass();
                if (c2Map.containsKey(c2MapKey)) {
                    Map<String, ClassTree> c3Map = c2Map.get(c2MapKey).getSubClass();
                    if (c3Map.containsKey(c3MapKey)) {
                        System.out.println("警告，" + c1MapKey + "-" + c2MapKey + "下存在重复三级分类代码");
                    } else {
                        if (StringUtils.isBlank(c3MapKey)) continue;
                        ClassTree c3Tree = new ClassTree();
                        c3Tree.setClassCode(c3Code);
                        c3Tree.setClassName(c3Name);
                        c3Map.put(c3MapKey, c3Tree);
                    }
                } else {
                    if (StringUtils.isBlank(c2MapKey)) continue;
                    ClassTree c2Tree = new ClassTree();
                    c2Tree.setClassCode(c2Code);
                    c2Tree.setClassName(c2Name);
                    c2Map.put(c2MapKey, c2Tree);

                    if (StringUtils.isBlank(c3MapKey)) continue;
                    ClassTree c3Tree = new ClassTree();
                    c3Tree.setClassCode(c3Code);
                    c3Tree.setClassName(c3Name);
                    c2Tree.getSubClass().put(c3MapKey, c3Tree);
                }
            } else {
                if (StringUtils.isBlank(c1MapKey)) continue;
                ClassTree c1Tree = new ClassTree();
                c1Tree.setClassCode(c1Code);
                c1Tree.setClassName(c1Name);
                c1Map.put(c1MapKey, c1Tree);

                if (StringUtils.isBlank(c2MapKey)) continue;
                ClassTree c2Tree = new ClassTree();
                c2Tree.setClassCode(c2Code);
                c2Tree.setClassName(c2Name);
                c1Tree.getSubClass().put(c2MapKey, c2Tree);

                if (StringUtils.isBlank(c3MapKey)) continue;
                ClassTree c3Tree = new ClassTree();
                c3Tree.setClassCode(c3Code);
                c3Tree.setClassName(c3Name);
                c2Tree.getSubClass().put(c3MapKey, c3Tree);
            }
        }
        return c1Map;
    }

    /**
     * classRowsToClassMap方法的逆运算
     * 将map分类树结构转换成逐条分类行
     *
     * @param classMap map分类树的
     * @return 逐条分类行
     */
    private List<ClassRow> classMapToClassRows(Map<String, ClassTree> classMap) {
        List<ClassRow> classRows = new ArrayList<>();
        for (Map.Entry<String, ClassTree> c1NameTree : classMap.entrySet()) {
            String c1Name = c1NameTree.getKey();
            ClassTree c1Tree = c1NameTree.getValue();

            ClassRow classRow = new ClassRow();
            classRow.setC1Code(c1Tree.getClassCode());
            classRow.setC1Name(c1Tree.getClassName());
            Map<String, ClassTree> c2NameMap = c1Tree.getSubClass();
            if (MapUtils.isEmpty(c2NameMap)) {
                classRows.add(classRow);
                classRow = new SecurityClassRow();
            } else {
                for (Map.Entry<String, ClassTree> c2NameTree : c2NameMap.entrySet()) {
                    String c2Name = c2NameTree.getKey();
                    ClassTree c2Tree = c2NameTree.getValue();
                    classRow.setC2Code(c2Tree.getClassCode());
                    classRow.setC2Name(c2Tree.getClassName());
                    Map<String, ClassTree> c3NameMap = c2Tree.getSubClass();
                    if (MapUtils.isEmpty(c3NameMap)) {
                        classRows.add(classRow);
                        classRow = classRow.copyC1();
                    } else {
                        for (Map.Entry<String, ClassTree> c3NameTree : c3NameMap.entrySet()) {
                            String c3Name = c3NameTree.getKey();
                            ClassTree c3Tree = c3NameTree.getValue();
                            classRow.setC3Code(c3Tree.getClassCode());
                            classRow.setC3Name(c3Tree.getClassName());
                            classRows.add(classRow);
                            classRow = classRow.copyC2();
                        }
                    }
                }
            }

        }
        return classRows;
    }

    public String getCellValue(XSSFCell cell) {
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
            cv = cv.substring(start + 1, end);
        }
        return rmBk(cv);
    }

    public String rmBk(String str) {
        if (str == null) return null;
        str = str.replaceAll("#N/A", "");
        str = str.replaceAll("\n", "");
        str = str.replaceAll("[()]", "");
        return str.replaceAll("\\b", "");
    }
}
