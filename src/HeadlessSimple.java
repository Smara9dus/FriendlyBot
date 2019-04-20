import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Query;
import org.gephi.filters.api.Range;
import org.gephi.filters.plugin.graph.DegreeRangeBuilder.DegreeRangeFilter;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;


public class HeadlessSimple {

    private String[] datasets = {"data/brunduart.graphml",
            "data/emma.thole.3.graphml",
            "data/emma.thole.600.graphml",
            "data/emma.thole.1000.graphml",
            "data/fabruxo.graphml",
            "data/flamespinner.graphml",
            "data/flamespinner1.graphml",
            "data/hermanomarcosm.graphml",
            "data/lostcirclenielsmain.graphml",
            "data/rodrigo.graphml",
            "data/ruan.felipe.5686.graphml",
            "data/ruan.felipe.5686_1.graphml",
            "data/zainmahmoodc.graphml"};

    private Random rand;

    private Workspace workspace;

    private UndirectedGraph graph;
    private GraphModel graphModel;
    private PreviewModel model;
    private ImportController importController;
    private FilterController filterController;
    private AppearanceController appearanceController;
    private AppearanceModel appearanceModel;

    public void script() {

        rand = new Random();

        setup();

        importData(datasets[rand.nextInt(datasets.length)]);
        //importData(datasets[6]);

        filter();

        layout(rand.nextInt(4));
        //layout(3);

        color();

        size();

        preview();

        export();
    }

    private void setup() {

        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();

        //Get models and controllers for this new workspace - will be useful later
        graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        model = Lookup.getDefault().lookup(PreviewController.class).getModel();
        importController = Lookup.getDefault().lookup(ImportController.class);
        filterController = Lookup.getDefault().lookup(FilterController.class);
        appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        appearanceModel = appearanceController.getModel();
    }


    private void importData(String filepath) {

        //Import file
        Container container;
        try {
            System.out.println("Filepath: " + filepath);
            File file = new File(getClass().getResource(filepath).toURI());
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);   //Force UNDIRECTED
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        graph = graphModel.getUndirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());
    }

    private void filter() {

        //Filter out floating nodes
        DegreeRangeFilter degreeFilter = new DegreeRangeFilter();
        degreeFilter.init(graph);
        degreeFilter.setRange(new Range(1, Integer.MAX_VALUE));
        Query query = filterController.createQuery(degreeFilter);
        GraphView view = filterController.filter(query);
        graphModel.setVisibleView(view);

        //See visible graph stats
        UndirectedGraph graphVisible = graphModel.getUndirectedGraphVisible();
        System.out.println("Nodes: " + graphVisible.getNodeCount());
        System.out.println("Edges: " + graphVisible.getEdgeCount());
    }


    private void layout(int option) {

        switch (option) {
            case 1: // ForceAtlas
                System.out.println("Layout: ForceAtlas");
                ForceAtlasLayout forceAtlas = new ForceAtlasLayout(null);
                forceAtlas.setGraphModel(graphModel);
                forceAtlas.resetPropertiesValues();

                forceAtlas.initAlgo();
                for (int i = 0; i < 100 && forceAtlas.canAlgo(); i++) {
                    forceAtlas.goAlgo();
                }
                forceAtlas.endAlgo();
                break;
            case 2: //ForceAtlas2
                System.out.println("Layout: ForceAtlas2");
                ForceAtlas2 forceAtlas2 = new ForceAtlas2(null);
                forceAtlas2.setGraphModel(graphModel);
                forceAtlas2.resetPropertiesValues();
                forceAtlas2.setStrongGravityMode(Boolean.TRUE);
                forceAtlas2.setGravity(0.2);

                forceAtlas2.initAlgo();
                for (int i = 0; i < 100 && forceAtlas2.canAlgo(); i++) {
                    forceAtlas2.goAlgo();
                }
                forceAtlas2.endAlgo();
                break;
            case 3: // Fruchterman Reingold
                System.out.println("Layout: Fruchterman Reingold");
                FruchtermanReingold fruchterman = new FruchtermanReingold(null);
                fruchterman.setGraphModel(graphModel);
                fruchterman.resetPropertiesValues();
                fruchterman.setGravity(100.0);
                fruchterman.setSpeed(10.0);

                fruchterman.initAlgo();
                for (int i = 0; i < 100 && fruchterman.canAlgo(); i++) {
                    fruchterman.goAlgo();
                }
                fruchterman.endAlgo();
                break;
            default: // Yifan Hu
                System.out.println("Layout: Yifan Hu");
                YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
                layout.setGraphModel(graphModel);
                layout.resetPropertiesValues();
                layout.setOptimalDistance(200f);

                layout.initAlgo();
                for (int i = 0; i < 100 && layout.canAlgo(); i++) {
                    layout.goAlgo();
                }
                layout.endAlgo();
                break;
        }
    }

    private void color() {

        Color color1 = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
        Color color2 = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));


        //Get Centrality
        GraphDistance distance = new GraphDistance();
        distance.setDirected(false);
        distance.execute(graphModel);

        //Rank color by Degree
        Function degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingElementColorTransformer.class);
        RankingElementColorTransformer degreeTransformer = degreeRanking.getTransformer();
        degreeTransformer.setColors(new Color[]{ color1, color2});
        degreeTransformer.setColorPositions(new float[]{0f, 1f});
        appearanceController.transform(degreeRanking);
    }

    private void size() {

        //Rank size by centrality
        Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
        Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn, RankingNodeSizeTransformer.class);
        RankingNodeSizeTransformer centralityTransformer = centralityRanking.getTransformer();
        centralityTransformer.setMinSize(3);
        centralityTransformer.setMaxSize(10);
        appearanceController.transform(centralityRanking);
    }

    private void preview() {

        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.FALSE);
        model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(EdgeColor.Mode.MIXED));
        model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 0.3f);
        model.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 0);
        model.getProperties().putValue(PreviewProperty.EDGE_CURVED, rand.nextBoolean());
        model.getProperties().putValue(PreviewProperty.EDGE_OPACITY, rand.nextFloat()*90 + 10);

        int r = rand.nextInt(10);
        if(r == 1) {
            model.getProperties().putValue(PreviewProperty.NODE_OPACITY, 0);
        } else if (r <= 3) {
            model.getProperties().putValue(PreviewProperty.NODE_OPACITY, rand.nextFloat()*100);
        }


        if(rand.nextInt(10) == 1) {
            Color c = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
            model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, c);
        } else {
            model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
        }
    }

    private void export() {

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("recent.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
    }
}
