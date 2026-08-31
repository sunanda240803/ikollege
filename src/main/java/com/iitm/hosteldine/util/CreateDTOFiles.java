package com.iitm.hosteldine.util;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"CallToPrintStackTrace", "MismatchedReadAndWriteOfArray"})
public class CreateDTOFiles {
    private static final String projectPath = System.getProperty("user.dir");
    private static final String directoryPath = projectPath + File.separator + "src" + File.separator + "main"
            + File.separator + "java" + File.separator + "com" + File.separator + "iitm" + File.separator + "hosteldine" + File.separator + "generated";
    private static final String basePackage = "com.iitm.hosteldine.generated.";
    private static final String[] include = {"FaqEntity"};
    private static final String[] exclude = {};
    private static final String[] excludeFields = {"created_by", "created_at", "modified_by", "modified_at", "active_flag",
            "createdBy", "createdAt", "modifiedBy", "modifiedAt", "activeFlag"};
    private static final char LF = '\n';
    private static final char statementEnd = ';';
    private static final String packageStatement = "package ";
    private static final String importStatement = "import ";
    private static final String packageModel = "model.";
    private static final String packageDto = "dto.";
    private static final String classSuffix = ".class";
    private static final String javaSuffix = ".java";
    private static final String blank = "";
    private static final String classStart = " {";
    private static final char classEnd = '}';
    private static final char openBrace = '(';
    private static final char closeBrace = ')';
    private static final char DQ = '\"';

    private static final String dto = "dto";
    private static final String dtoCap = "Dto";
    private static final String mapper = "mapper";
    private static final String mapperCap = "Mapper";
    private static final String repository = "repository";
    private static final String repositoryCap = "Repository";
    private static final String model = "model";
    private static final String entityCap = "Entity";

    public static void main(String[] args) {
        // Directory path where files should be created
        //noinspection UnnecessaryLocalVariable
        String createType = dto;
        copyContent(createType);
    }

    private static void copyContent(String createType) {
        String fromPackage = model;
        String fromPrefix = blank;
        String fromSuffix = entityCap;
        ArrayList<String> toPackageList = new ArrayList<>();
        ArrayList<String> classTypes = new ArrayList<>();
        ArrayList<String> toPrefixList = new ArrayList<>();
        ArrayList<String> toSuffixList = new ArrayList<>();
        if (createType.equals(dto)) {
            fromPackage = model;
            fromPrefix = blank;
            fromSuffix = entityCap;
            toPrefixList = new ArrayList<>(Arrays.asList(blank, blank, blank));
            toSuffixList = new ArrayList<>(Arrays.asList(dtoCap, mapperCap, repositoryCap));
            classTypes = new ArrayList<>(Arrays.asList(dto, mapper, repository));
            toPackageList = new ArrayList<>(Arrays.asList(dto, mapper, repository));
        }
        ArrayList<File> filesList = getFileList(fromPackage, fromPrefix, fromSuffix, Arrays.asList(include), Arrays.asList(exclude));
        System.out.println("Total Files to process: " + filesList.size());
        int totalFilesSuccessful = 0;
        int totalFilesCreated = 0;
        if (!filesList.isEmpty()) {
            for (File file : filesList) {
                String fileName = file.getName().replace(javaSuffix, blank);
                System.out.println("\nProcessing " + fileName);
                int resultState = 0;
                for (int i = 0; i < classTypes.size(); i++) {
                    String classType = classTypes.get(i);
                    String toPrefix = toPrefixList.get(i);
                    String toSuffix = toSuffixList.get(i);
                    String toPackage = toPackageList.get(i);
                    boolean result = false;
                    switch (classType) {
                        case dto:
                            updateEntityClass(file);
                            result = createDtoClass(file, fromSuffix, toSuffix, fromPrefix, toPrefix, toPackage);
                            break;
                        case mapper:
                            if (fileName.endsWith(fromSuffix)) {
                                result = createMapperClass(file, fromSuffix, toSuffix, fromPrefix, toPrefix, toPackage);
                            }
                            break;
                        case repository:
                            if (fileName.endsWith(fromSuffix)) {
                                result = createRepositoryClass(file, fromSuffix, toSuffix, fromPrefix, toPrefix, toPackage);
                            }
                            break;
                    }
                    if (result) {
                        resultState++;
                        totalFilesCreated++;
                    }
                }
                if (resultState == classTypes.size()) {
                    System.out.println("\nSubordinate classes created successfully for " + fileName + " .");
                    totalFilesSuccessful++;
                } else if (resultState > 0) {
                    System.out.println("\nAble to create only " + resultState + " Subordinate class(es) for " + fileName + " .");
                } else {
                    System.out.println("\nError processing " + fileName + ".");
                }
            }
            if (totalFilesSuccessful == filesList.size()) {
                System.out.println("All files created successfully.");
            } else {
                System.out.println("Error creating some or all files.");
            }
            System.out.println("Total Files processed: " + filesList.size());
            System.out.println("New Files created: " + totalFilesCreated);
        }
    }

    private static final String defaultMapperImports = """
            import org.mapstruct.Mapper;
            import org.mapstruct.Mapping;
            import org.mapstruct.factory.Mappers;
            """;
    private static final String defaultMappingStr = "    @Mapping(target = \".\", source = \".\")" + LF;
    private static final String mapperDefaultIgnore = """
                @Mapping(target = "createdBy", ignore = true)
                @Mapping(target = "createdAt", ignore = true)
                @Mapping(target = "modifiedBy", ignore = true)
                @Mapping(target = "modifiedAt", ignore = true)
                @Mapping(target = "activeFlag", ignore = true)
            """;
    private static final String tabSpace = "    ";
    private static final String space = " ";
    private static final String mapperAnnotation = "@Mapper";
    private static final String jsonPropertyAnnotation = "@JsonProperty";
    private static final String interfaceDeclaration = "public interface ";
    private static final String classDeclaration = "public class ";
    private static final String mapperInstance = " INSTANCE = Mappers.getMapper";
    private static final String mapperFrom = "Dto from";
    private static final String mapperFromTo = " model";
    private static final String mapperTo = " to";
    private static final String mapperToTo = "Dto modelDto";

    private static void updateEntityClass(File file) {
        StringBuilder classContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
            String line = br.readLine();
            while (line != null) {
                if (line.startsWith("package ")) {
                    line += """
                            \n
                            import com.iitm.hosteldine.constant.ModelConstants;
                            import com.iitm.hosteldine.entity.CommonEntity;""";
                }
                if (line.startsWith("@Table") && !line.endsWith("SCHEMA)")) {
                    line = line.replace(")", ", schema = ModelConstants.SCHEMA)");
                }
                if (line.startsWith("public class") && !line.endsWith("CommonEntity {")) {
                    line = line.replace(" {", " extends CommonEntity {");
                }
                boolean hasField = false;
                for (String fieldName : excludeFields) {
                    if (line.contains(fieldName)) {
                        hasField = true;
                        break;
                    }
                }
                if (!hasField) {
                    classContent.append(line);
                    classContent.append(System.lineSeparator());
                }
                line = br.readLine();
            }
            createAndWriteToFile(file.getPath(), classContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean createMapperClass(File file, String fromSuffix, String toSuffix, String fromPrefix, String toPrefix, String toPackage) {
        StringBuilder classContent = new StringBuilder();
        String fromClassName = file.getName().replace(javaSuffix, blank);
        try {
            String model = fromClassName.replace(fromPrefix, blank).replace(fromSuffix, blank);
            String newClassName = fromClassName.replace(fromSuffix, toSuffix).replace(fromPrefix, toPrefix);
            String dtoClassName = fromClassName.replace(fromSuffix, dtoCap).replace(fromPrefix, toPrefix);
            classContent.append(packageStatement).append(basePackage).append(toPackage).append(statementEnd).append(LF).append(LF);
            classContent.append(defaultMapperImports).append(LF);
            classContent.append(importStatement).append(basePackage).append(packageModel).append(fromClassName).append(statementEnd).append(LF);
            classContent.append(importStatement).append(basePackage).append(packageDto).append(dtoClassName).append(statementEnd).append(LF).append(LF);
            classContent.append(mapperAnnotation).append(LF);
            classContent.append(interfaceDeclaration).append(newClassName).append(classStart).append(LF);
            classContent.append(tabSpace).append(newClassName).append(mapperInstance).append(openBrace).append(newClassName).append(classSuffix).append(closeBrace);
            classContent.append(statementEnd).append(LF).append(LF);
            classContent.append(defaultMappingStr);
            classContent.append(tabSpace).append(model).append(mapperFrom).append(fromClassName).append(openBrace);
            classContent.append(fromClassName).append(mapperFromTo).append(closeBrace).append(statementEnd).append(LF).append(LF);
            classContent.append(defaultMappingStr);
            classContent.append(mapperDefaultIgnore);
            classContent.append(tabSpace).append(fromClassName).append(mapperTo).append(fromClassName).append(openBrace);
            classContent.append(model).append(mapperToTo).append(closeBrace).append(statementEnd).append(LF);
            classContent.append(classEnd);
            String classPath = directoryPath + File.separator + toPackage + File.separator + newClassName + javaSuffix;
            return createAndWriteToFile(classPath, classContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final String privateAccess = "private ";
    private static final String defaultDtoImports = """
            import lombok.Data;
            import lombok.Builder;
            """;
    private static final String defaultRepositoryImports = """
            import org.springframework.data.jpa.repository.JpaRepository;
            import org.springframework.data.jpa.repository.Query;
            
            import java.util.List;
            import java.util.Optional;
            """;
    private static final String dtoAnnotations = """
            @Data
            @Builder
            """;

    private static boolean createDtoClass(File file, String fromSuffix, String toSuffix, String fromPrefix, String toPrefix, String toPackage) {
        StringBuilder classContent = new StringBuilder();
        String fromClassName = file.getName().replace(javaSuffix, blank);
        try {
            Class<?> entitiyClass = Class.forName(basePackage + packageModel + fromClassName);
            System.out.println("\nclass found");
            String newClassName = fromClassName.replace(fromSuffix, toSuffix).replace(fromPrefix, toPrefix);
            StringBuilder fieldsContent = new StringBuilder();
            ArrayList<String> fieldType = new ArrayList<>();
            for (Field field : entitiyClass.getDeclaredFields()) {
                String fieldName = field.getName();
                String[] typeSplit = field.getType().getName().split(Pattern.quote("."));
                String type;
                if (typeSplit.length > 1) {
                    type = typeSplit[typeSplit.length - 1];
                } else {
                    type = field.getType().getName();
                    if (type.equals("[B")) type = "byte[]";
                }
                fieldsContent
//                        .append(tabSpace).append(jsonPropertyAnnotation).append(openBrace).append(DQ).append(fieldName).append(DQ).append(closeBrace).append(LF)
                        .append(tabSpace).append(privateAccess).append(type).append(space).append(fieldName).append(statementEnd).append(LF)
                ;
                if (!fieldType.contains(field.getType().getName())) {
                    fieldType.add(field.getType().getName());
                }
            }
            StringBuilder importContent = new StringBuilder();
            importContent.append(defaultDtoImports);
            for (String type : fieldType) {
                if (type.contains(".") && !type.equals("java.lang.String")) {
                    importContent.append(importStatement).append(type).append(statementEnd).append(LF);
                }
            }
            importContent.append(LF);
            classContent.append(packageStatement).append(basePackage).append(toPackage).append(statementEnd).append(LF).append(LF);
            classContent.append(importContent);
            classContent.append(dtoAnnotations);
            classContent.append(classDeclaration).append(newClassName).append(classStart).append(LF);
            classContent.append(fieldsContent);
            classContent.append(classEnd);
            String classPath = directoryPath + File.separator + toPackage + File.separator + newClassName + javaSuffix;
            return createAndWriteToFile(classPath, classContent.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static final String repositoryExtendStart = " extends JpaRepository<";
    private static final String repositoryExtendEnd = ", Long>";
    private static final String optionalQueryStart = "Optional<";
    private static final String optionalQueryEnd = "> findByIdAndActiveFlag(Long id, String activeFlag)";
    private static final String listQueryStart = "List<";
    private static final String listQueryEnd = "> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag)";
    private static final String queryAnnotation = "@Query(value = ";
    private static final String countSelectQuery = "select count(x) from ";
    private static final String countWhereClause = " x where x.activeFlag = :activeFlag";
    private static final String countReturnType = "Long getActiveCount(String activeFlag)";

    private static boolean createRepositoryClass(File file, String fromSuffix, String toSuffix, String fromPrefix, String toPrefix, String toPackage) {
        StringBuilder classContent = new StringBuilder();
        String fromClassName = file.getName().replace(javaSuffix, blank);
        try {
            String newClassName = fromClassName.replace(fromSuffix, toSuffix).replace(fromPrefix, toPrefix);
            classContent.append(packageStatement).append(basePackage).append(toPackage).append(statementEnd).append(LF).append(LF);
            classContent.append(importStatement).append(basePackage).append(packageModel).append(fromClassName).append(statementEnd).append(LF);
            classContent.append(defaultRepositoryImports).append(LF);
            classContent.append(interfaceDeclaration).append(newClassName).append(repositoryExtendStart).append(fromClassName);
            classContent.append(repositoryExtendEnd).append(classStart).append(LF);
            classContent.append(tabSpace).append(optionalQueryStart).append(fromClassName).append(optionalQueryEnd).append(statementEnd).append(LF).append(LF);
            classContent.append(tabSpace).append(listQueryStart).append(fromClassName).append(listQueryEnd).append(statementEnd).append(LF).append(LF);
            classContent.append(tabSpace).append(queryAnnotation).append(DQ).append(DQ).append(DQ).append(LF);
            classContent.append(tabSpace).append(tabSpace).append(tabSpace).append(tabSpace).append(countSelectQuery).append(fromClassName).append(countWhereClause).append(LF);
            classContent.append(tabSpace).append(tabSpace).append(tabSpace).append(DQ).append(DQ).append(DQ).append(closeBrace).append(LF);
            classContent.append(tabSpace).append(countReturnType).append(statementEnd).append(LF);
            classContent.append(classEnd);
            String classPath = directoryPath + File.separator + toPackage + File.separator + newClassName + javaSuffix;
            return createAndWriteToFile(classPath, classContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static ArrayList<File> getFileList(String fromPackage, String prefix, String suffix, List<String> include, List<String> exclude) {
        String packagePath = directoryPath + "/" + fromPackage;
        File file = new File(packagePath);
        ArrayList<File> filesList = new ArrayList<>();
        if (file.exists()) {
            filesList.addAll(getFiles(file, prefix, suffix, include, exclude));
        }
        return filesList;
    }

    private static Collection<? extends File> getFiles(File parentFile, String prefix, String suffix, List<String> include, List<String> exclude) {
        ArrayList<File> list = new ArrayList<>();
        if (parentFile.isDirectory()) {
            if (parentFile.listFiles() != null) {
                for (File file : Objects.requireNonNull(parentFile.listFiles())) {
                    list.addAll(getFiles(file, prefix, suffix, include, exclude));
                }
            }
        } else {
            String fileName = parentFile.getName().replace(javaSuffix, blank);
            if ((prefix.isEmpty() && suffix.isEmpty()) ||
                    (!prefix.isEmpty() && fileName.startsWith(prefix)) ||
                    (!suffix.isEmpty() && fileName.endsWith(suffix))) {
                if ((include.isEmpty() && exclude.isEmpty()) || include.contains(fileName) || (include.isEmpty() && !exclude.contains(fileName))) {
                    list.add(parentFile);
                }
            }
        }
        return list;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static boolean createAndWriteToFile(String filePath, String content) {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
            System.out.println("File created successfully: " + filePath.replace(directoryPath, blank));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static String readContent(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
