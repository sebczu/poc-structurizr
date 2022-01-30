package com.sebczu.poc.structurizr.application;

import com.structurizr.Workspace;
import com.structurizr.documentation.DecisionStatus;
import com.structurizr.documentation.Documentation;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.InteractionStyle;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.PaperSize;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.ViewSet;
import java.io.File;
import java.util.Date;

public class Main {

  public static void main(String[] args) throws Exception {
    Workspace workspace = new Workspace("Workspace name", "workspace description");

    // model
    Model model = workspace.getModel();

    Person user = model.addPerson("User", "User description");
    Person user2 = model.addPerson("User2", "User description");
    SoftwareSystem softwareSystem = model.addSoftwareSystem("Software System name", "Software System description");
    Container container = softwareSystem.addContainer("Container", "Container description");
    container.setTechnology("java");
    Component component1 = container.addComponent("Component1", "Component description", "java");
    Component component2 = container.addComponent("Component2", "Component description", "java");
    component1.uses(component2, "relation");
    user.uses(container, "uses", "HTTP", InteractionStyle.Asynchronous);
    user2.uses(softwareSystem, "uses", "HTTP", InteractionStyle.Synchronous);

    // views
    ViewSet views = workspace.getViews();

    ComponentView componentView = views.createComponentView(container, "component view key", "component view description");
    componentView.setPaperSize(PaperSize.A5_Landscape);
    componentView.addAllComponents();

    ContainerView containerView = views.createContainerView(softwareSystem, "container view key", "container view description");
    containerView.setPaperSize(PaperSize.A5_Landscape);
    containerView.addAllContainers();

    SystemContextView systemContextView = views.createSystemContextView(softwareSystem, "system context view key", "system context view description");
    systemContextView.setPaperSize(PaperSize.A5_Landscape);
    systemContextView.addAllSoftwareSystems();
    systemContextView.addAllPeople();
    systemContextView.addDefaultElements();

    SystemLandscapeView systemLandscapeView = views.createSystemLandscapeView("system landscape view key", "system landscape view description");
    systemLandscapeView.addAllElements();
    systemLandscapeView.enableAutomaticLayout();

    // documentation
    StructurizrDocumentationTemplate template = new StructurizrDocumentationTemplate(workspace);
    template.addContextSection(softwareSystem, Format.Markdown,
        "Some content\n" +
            "\n" +
            "![](embed:SystemContext)\n" +
            "Some content"
    );

    // ADR
    Documentation documentation = workspace.getDocumentation();
    documentation.addDecision("id", new Date(), "title", DecisionStatus.Accepted, Format.Markdown, "content");
    documentation.addDecision("id2", new Date(), "title2", DecisionStatus.Rejected, Format.Markdown, "content2");
    documentation.addDecision(softwareSystem, "id3", new Date(), "title3", DecisionStatus.Rejected, Format.Markdown, "content3");

    // styling
    Styles styles = views.getConfiguration().getStyles();
    styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
    styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);

    // generate workspace.json
    File file = new File("docker/volume/workspace.json");
    if (file.exists()) {
      Workspace existingWorkspace = WorkspaceUtils.loadWorkspaceFromJson(file); // load the old version that contains layout information
      workspace.getViews().copyLayoutInformationFrom(existingWorkspace.getViews()); // copy layout information into the new workspace
    }
    WorkspaceUtils.saveWorkspaceToJson(workspace, file);
  }

}
