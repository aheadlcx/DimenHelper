package me.aheadlcx.dimenhelper;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import me.aheadlcx.dimenhelper.out.OutUtils;
import me.aheadlcx.dimenhelper.out.OutValues;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aheadlcx
 * 2016/10/17 下午5:32.
 */
public class MainAction extends BaseGenerateAction {
    private Project project;

    public MainAction() {
        this(null);
    }

    public MainAction(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        String canonicalPath = getCurFilePath(anActionEvent);
        if (TextUtils.isEmpty(canonicalPath)){
            return;
        }
        File file = new File(canonicalPath);
        List<OriginDimen> originDimens = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            originDimens = domXml(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String firstOutput = "";
        VirtualFile baseFile = project.getBaseDir();

        VirtualFile[] childFiles = baseFile.getChildren();

        if (childFiles.length > 0) {
            firstOutput = getOutputPath(childFiles);
            if (firstOutput == null) {
                firstOutput = baseFile.getCanonicalPath();
            }
        } else {
            firstOutput = baseFile.getCanonicalPath();
        }

        List<OutValues> outValues = OutUtils.getOutValues();
        for (int i = 0; i < outValues.size(); i++) {
            OutValues values = outValues.get(i);
            File dimenFile = OutUtils.createDimenFile(firstOutput, values);
            try {
                OutUtils.writeDimen(dimenFile, originDimens, values);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        String projectbuildFilePath = "";
//        VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(new File(projectbuildFilePath));
//        vf.refresh(true, false);
        baseFile.refresh(true, false);
    }

    private String getCurFilePath(AnActionEvent anActionEvent) {
        project = anActionEvent.getProject();
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        return psiFile.getVirtualFile().getCanonicalPath();
    }

    private List<OriginDimen> domXml(InputStream inputStream) {
        List<OriginDimen> originDimens = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Element rootElement = document.getDocumentElement();
            NodeList items = rootElement.getElementsByTagName("dimen");
            int length = items.getLength();
            System.out.println("length = " + length);

            for (int i = 0; i < length; i++) {
                Element item = (Element) items.item(i);
                OriginDimen originDimen = Utils.translate(item);
                originDimens.add(originDimen);
            }

        } catch (Exception e) {
            System.out.println("Exception = " + e.toString() + e.getMessage());
            e.printStackTrace();
        }
        return originDimens;
    }

    @Override
    protected void update(@NotNull Presentation presentation, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        super.update(presentation, project, editor, file);
        presentation.setVisible(true);
    }

    @Override
    protected void update(@NotNull Presentation presentation, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull DataContext dataContext, @Nullable String actionPlace) {
        super.update(presentation, project, editor, file, dataContext, actionPlace);
        presentation.setVisible(true);
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        try {
            Presentation presentation = e.getPresentation();
            if (presentation == null)return;
            String curFilePath = getCurFilePath(e);
            System.out.println("curFilePath = " + curFilePath);
            if (curFilePath == null || !curFilePath.contains("xml")) {
                presentation.setEnabled(false);
                presentation.setVisible(false);
            } else if (presentation != null){
                presentation.setVisible(true);
                presentation.setEnabled(true);
            }
        } catch (Exception e1) {
            System.out.println("error  = " + e1.getMessage());
            e1.printStackTrace();
        }
    }

    private String getOutputPath(VirtualFile[] virtualFiles) {
        for (VirtualFile virtualFile : virtualFiles) {
            String name = virtualFile.getName();
            VirtualFile[] childVirtualFile = virtualFile.getChildren();

            if (name.equals("res")) {
                return virtualFile.getCanonicalPath();
            } else if (childVirtualFile.length > 0) {
                String resPath = getOutputPath(childVirtualFile);
                if (resPath != null) {
                    return resPath;
                }
            }
        }
        return null;
    }
}
