import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import org.gephi.appearance.api.*;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.RankingElementColorTransformer;
import org.gephi.appearance.plugin.RankingNodeSizeTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
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
import org.gephi.io.exporter.preview.PNGExporter;
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
import org.gephi.statistics.plugin.*;
import org.openide.util.Lookup;                     // TODO: Replace this since it is triggering warnings at runtime?


public class HeadlessSimple {

    private String[] datasets = {"data/brunduart.graphml", // TODO: Data cleanup (remove unused data, make anonymous?)
            "data/emma3.graphml",
            "data/emma600.graphml",
            "data/emma1000.graphml",
            "data/erin.graphml",
            "data/fabruxo.graphml",
            "data/flamespinner.graphml",
            "data/flamespinner1.graphml",
            "data/hermanomarcosm.graphml",
            "data/jordan.harris.1997 (5).graphml",
            "data/lostcirclenielsmain.graphml",
            "data/malcolm.stuart.319.graphml",
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

    public String script() {

        rand = new Random();

        // get randoms (make this a separate function once recommender is up)

        int options[] = new int[0];
        try {
            options = getOptions();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        setup();

        importData(datasets[rand.nextInt(datasets.length)]);

        filter();

        layout(options[0]);

        color(options[1]);

        size(options[2]);

        preview(options[3], options[4], options[5]);

        export("png"); // can also be "pdf"

        return (String.valueOf(options[0]) + ' ' + options[1] + ' ' + options[2] + ' ' + options[3] + ' ' + options[4] + ' ' + options[5]);
    }

    private int[] getOptions() throws FileNotFoundException {
        int options[] = new int [6];
        int next;

        // without using twitter likes
        //options[0] = rand.nextInt(5);
        //options[1] = rand.nextInt(5);
        //options[2] = rand.nextInt(5);
        //options[3] = rand.nextInt(2);
        //options[4] = rand.nextInt(4);
        //options[5] = rand.nextInt(3);

        // with twitter likes
        int likes[] = new int[24];

        File file = new File("likes.txt");

        Scanner s = new Scanner(file);

        for (int i = 0; i < 24; i++) {
            likes[i] = Integer.parseInt(s.next());
        }

        next = rand.nextInt(likes[0] + likes[1] + likes[2] + likes[3] + likes[4]);
        if (next < likes[0]) {
            options[0] = 0;
        } else if (next < likes[0] + likes[1]) {
            options[0] = 1;
        } else if (next < likes[0] + likes[1] + likes[2]) {
            options[0] = 2;
        } else if (next < likes[0] + likes[1] + likes[2] + likes[3]) {
            options[0] = 3;
        } else {
            options[0] = 4;
        }

        next = rand.nextInt(likes[5] + likes[6] + likes[7] + likes[8] + likes[9]);
        if (next < likes[5]) {
            options[1] = 0;
        } else if (next < likes[5] + likes[6]) {
            options[1] = 1;
        } else if (next < likes[5] + likes[6] + likes[7]) {
            options[1] = 2;
        } else if (next < likes[5] + likes[6] + likes[7] + likes[8]) {
            options[1] = 3;
        } else {
            options[1] = 4;
        }

        next = rand.nextInt(likes[10] + likes[11] + likes[12] + likes[13] + likes[14]);
        if (next < likes[10]) {
            options[2] = 0;
        } else if (next < likes[10] + likes[11]) {
            options[2] = 1;
        } else if (next < likes[10] + likes[11] + likes[12]) {
            options[2] = 2;
        } else if (next < likes[10] + likes[11] + likes[12] + likes[13]) {
            options[2] = 3;
        } else {
            options[2] = 4;
        }

        next = rand.nextInt(likes[15] + likes[16]);
        if (next < likes[15]) {
            options[3] = 0;
        } else {
            options[3] = 1;
        }

        next = rand.nextInt(likes[17] + likes[18] + likes[19] + likes[20]);
        if (next < likes[17]) {
            options[4] = 0;
        } else if (next < likes[17] + likes[18]) {
            options[4] = 1;
        } else if (next < likes[17] + likes[18] + likes[19]) {
            options[4] = 2;
        } else {
            options[4] = 3;
        }

        next = rand.nextInt(likes[21] + likes[22] + likes[23]);
        if (next < likes[21]) {
            options[5] = 0;
        } else if (next < likes[21] + likes[22]) {
            options[5] = 1;
        } else {
            options[5] = 2;
        }

        return options;
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


    private void importData(String filepath) { // TODO: Merge duplicate edges at minimum weight

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
        //System.out.println("Nodes: " + graph.getNodeCount());
        //System.out.println("Edges: " + graph.getEdgeCount());

        System.out.println("Data imported");
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
        //System.out.println("Nodes: " + graphVisible.getNodeCount());
        //System.out.println("Edges: " + graphVisible.getEdgeCount());

        System.out.println("Data filtered");
    }


    private void layout(int option) {

        int iters = rand.nextInt(60)+70;

        switch (option) {
            case 1: // ForceAtlas
                //System.out.println("Layout: ForceAtlas");
                ForceAtlasLayout forceAtlas = new ForceAtlasLayout(null);
                forceAtlas.setGraphModel(graphModel);
                forceAtlas.resetPropertiesValues();

                forceAtlas.initAlgo();
                for (int i = 0; i <  iters && forceAtlas.canAlgo(); i++) {
                    forceAtlas.goAlgo();
                }
                forceAtlas.endAlgo();
                break;
            case 2: //ForceAtlas2
                //System.out.println("Layout: ForceAtlas2");
                ForceAtlas2 forceAtlas2 = new ForceAtlas2(null);
                forceAtlas2.setGraphModel(graphModel);
                forceAtlas2.resetPropertiesValues();
                forceAtlas2.setStrongGravityMode(Boolean.TRUE);
                forceAtlas2.setGravity(0.2);

                forceAtlas2.initAlgo();
                for (int i = 0; i < iters && forceAtlas2.canAlgo(); i++) {
                    forceAtlas2.goAlgo();
                }
                forceAtlas2.endAlgo();
                break;
            case 3: // Fruchterman Reingold
                //System.out.println("Layout: Fruchterman Reingold");

                FruchtermanReingold fruchterman = new FruchtermanReingold(null);
                fruchterman.setGraphModel(graphModel);
                fruchterman.resetPropertiesValues();
                fruchterman.setGravity(100.0);
                fruchterman.setSpeed(10.0);

                fruchterman.initAlgo();
                for (int i = 0; i < iters && fruchterman.canAlgo(); i++) {
                    fruchterman.goAlgo();
                }
                fruchterman.endAlgo();
                break;
            case 4:
                //System.out.println("Layout: ForceAtlas2 + Fruchterman Reingold");
                ForceAtlas2 prefruchterman = new ForceAtlas2(null);
                prefruchterman.setGraphModel(graphModel);
                prefruchterman.resetPropertiesValues();
                prefruchterman.setStrongGravityMode(Boolean.TRUE);
                prefruchterman.setGravity(0.2);

                prefruchterman.initAlgo();
                for (int i = 0; i < 100 && prefruchterman.canAlgo(); i++) {
                    prefruchterman.goAlgo();
                }
                prefruchterman.endAlgo();


                FruchtermanReingold fruchterman2 = new FruchtermanReingold(null);
                fruchterman2.setGraphModel(graphModel);
                fruchterman2.resetPropertiesValues();
                fruchterman2.setGravity(100.0);
                fruchterman2.setSpeed(10.0);

                fruchterman2.initAlgo();
                for (int i = 0; i < iters && fruchterman2.canAlgo(); i++) {
                    fruchterman2.goAlgo();
                }
                fruchterman2.endAlgo();
                break;
            default: // Yifan Hu
                //System.out.println("Layout: Yifan Hu");
                YifanHuLayout layout = new YifanHuLayout(null, new StepDisplacement(1f));
                layout.setGraphModel(graphModel);
                layout.resetPropertiesValues();
                layout.setOptimalDistance(200f);

                layout.initAlgo();
                for (int i = 0; i < iters && layout.canAlgo(); i++) {
                    layout.goAlgo();
                }
                layout.endAlgo();
                break;
        }

        System.out.println("Layout complete");
    }

    private void color(int option) {  // TODO: Make a custom palette generator?
                                      // TODO: Color scales of 2-4 colors rather than just 2
        Color color1, color2;

        if(rand.nextInt(8)==1){ // Chance to choose grayscale
            int r1 = rand.nextInt(255);
            int r2 = rand.nextInt(255);
            color1 =  new Color(r1,r1,r1);
            color2 =  new Color(r2,r2,r2);
        } else {
            color1 = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
            color2 = new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
        }

        switch(option) {
            case 1: { // Rank color by Eigenvector Centrality
                //System.out.println("Color: Eigenvector Centrality");
                EigenvectorCentrality eigen = new EigenvectorCentrality();
                eigen.execute(graphModel);
                Column eigenColumn = graphModel.getNodeTable().getColumn(EigenvectorCentrality.EIGENVECTOR);
                Function eigenRanking = appearanceModel.getNodeFunction(graph, eigenColumn, RankingElementColorTransformer.class);
                RankingElementColorTransformer eigenTransformer = eigenRanking.getTransformer();
                eigenTransformer.setColors(new Color[]{color1, color2});
                eigenTransformer.setColorPositions(new float[]{0f, 1f});
                appearanceController.transform(eigenRanking);
                break;
            }
            case 2: { // Rank color by Centrality
                //System.out.println("Color: Centrality");
                GraphDistance distance = new GraphDistance();
                distance.setDirected(false);
                distance.execute(graphModel);
                Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
                Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn, RankingElementColorTransformer.class);
                RankingElementColorTransformer centralityTransformer = centralityRanking.getTransformer();
                centralityTransformer.setColors(new Color[]{color1, color2});
                centralityTransformer.setColorPositions(new float[]{0f, 1f});
                appearanceController.transform(centralityRanking);
                break;
            }
            case 3: { // Rank color by PageRank
                //System.out.println("Color: PageRank");
                PageRank pageRank = new PageRank();
                pageRank.execute(graphModel);
                Column pageRankColumn = graphModel.getNodeTable().getColumn(PageRank.PAGERANK);
                Function pageRankRanking = appearanceModel.getNodeFunction(graph, pageRankColumn, RankingElementColorTransformer.class);
                RankingElementColorTransformer pageRankTransformer = pageRankRanking.getTransformer();
                pageRankTransformer.setColors(new Color[]{color1, color2});
                pageRankTransformer.setColorPositions(new float[]{0f, 1f});
                appearanceController.transform(pageRankRanking);
                break;
            }
            case 4: { // Rank color by degree
                //System.out.println("Color: Degree");
                Function degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingElementColorTransformer.class);
                RankingElementColorTransformer degreeTransformer = degreeRanking.getTransformer();
                degreeTransformer.setColors(new Color[]{ color1, color2});
                degreeTransformer.setColorPositions(new float[]{0f, 1f});
                appearanceController.transform(degreeRanking);
                break;
            }
            default: {
                //System.out.println("Color: Modularity Class");
                Modularity modularity = new Modularity();
                modularity.setUseWeight(Boolean.FALSE);
                modularity.setRandom(Boolean.TRUE);
                modularity.setResolution(5.0);
                modularity.execute(graphModel);
                Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
                Function func2 = appearanceModel.getNodeFunction(graph, modColumn, PartitionElementColorTransformer.class);
                Partition partition2 = ((PartitionFunction) func2).getPartition();
                Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
                partition2.setColors(palette2.getColors());
                appearanceController.transform(func2);
            }
        }

        System.out.println("Color transform complete");
    }

    private void size(int option) {

        int minSize = rand.nextInt(2) + 1;
        int maxSize = rand.nextInt(12) + 8;

        switch(option) {
            case 1: { // Rank size by Eigenvector Centrality
                //System.out.println("Size: Eigenvector Centrality");
                EigenvectorCentrality eigen = new EigenvectorCentrality();
                eigen.execute(graphModel);
                Column eigenColumn = graphModel.getNodeTable().getColumn(EigenvectorCentrality.EIGENVECTOR);
                Function eigenRanking = appearanceModel.getNodeFunction(graph, eigenColumn, RankingNodeSizeTransformer.class);
                RankingNodeSizeTransformer eigenTransformer = eigenRanking.getTransformer();
                eigenTransformer.setMinSize(minSize);
                eigenTransformer.setMaxSize(maxSize);
                appearanceController.transform(eigenRanking);
                break;
            }
            case 2: { // Rank size by Centrality
                //System.out.println("Size: Centrality");
                GraphDistance distance = new GraphDistance();
                distance.setDirected(false);
                distance.execute(graphModel);
                Column centralityColumn = graphModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
                Function centralityRanking = appearanceModel.getNodeFunction(graph, centralityColumn, RankingNodeSizeTransformer.class);
                RankingNodeSizeTransformer centralityTransformer = centralityRanking.getTransformer();
                centralityTransformer.setMinSize(minSize);
                centralityTransformer.setMaxSize(maxSize);
                appearanceController.transform(centralityRanking);
                break;
            }
            case 3: { // Rank size by PageRank
                //System.out.println("Size: PageRank");
                PageRank pageRank = new PageRank();
                pageRank.execute(graphModel);
                Column pageRankColumn = graphModel.getNodeTable().getColumn(PageRank.PAGERANK);
                Function pageRankRanking = appearanceModel.getNodeFunction(graph, pageRankColumn, RankingNodeSizeTransformer.class);
                RankingNodeSizeTransformer pageRankTransformer = pageRankRanking.getTransformer();
                pageRankTransformer.setMinSize(minSize);
                pageRankTransformer.setMaxSize(maxSize);
                appearanceController.transform(pageRankRanking);
                break;
            }
            case 4: { // Rank size by degree
                //System.out.println("Size: Degree");
                Function degreeRanking = appearanceModel.getNodeFunction(graph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingNodeSizeTransformer.class);
                RankingNodeSizeTransformer degreeTransformer = degreeRanking.getTransformer();
                degreeTransformer.setMinSize(minSize);
                degreeTransformer.setMaxSize(maxSize);
                appearanceController.transform(degreeRanking);
                break;
            }
            default: {
                //System.out.println("No Size Ranking");
                break;
            }
        }

        System.out.println("Size transform complete");
    }

    private void preview(int option1, int option2, int option3) {

        model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.FALSE);
        model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(EdgeColor.Mode.MIXED));
        model.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 0);

        // curved / straight edges (this is being done in a really dumb way for consistency

        switch(option1) {
            case 1:
                model.getProperties().putValue(PreviewProperty.EDGE_CURVED, true);
                break;
            default:
                model.getProperties().putValue(PreviewProperty.EDGE_CURVED, false);
                break;
        }

        // node/edge visibility
        switch(option2) {
            case 1:
                model.getProperties().putValue(PreviewProperty.NODE_OPACITY, 0.01);
                model.getProperties().putValue(PreviewProperty.EDGE_OPACITY, rand.nextFloat()*100);
                model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, rand.nextFloat()*14.9+.1f);
                break;
            case 2:
                model.getProperties().putValue(PreviewProperty.NODE_OPACITY, rand.nextFloat()*100);
                model.getProperties().putValue(PreviewProperty.EDGE_OPACITY, rand.nextFloat()*100);
                model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, rand.nextFloat()*0.9+.1f);
                break;
            case 3:
                model.getProperties().putValue(PreviewProperty.EDGE_OPACITY, rand.nextFloat()*90 + 10);
                model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, rand.nextFloat()*0.9+.1f);
                break;
            default:
                model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, rand.nextFloat()*0.9+.1f);
                break;
        }

        // background color
        switch(option3) {
            case 1:
                model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
                break;
            case 2:
                model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
                break;
            default:
                Color c = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
                model.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, c);
                break;
        }

        System.out.println("Exporting...");
    }

    private void export(String exportType) {

        //Export
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);

        if (exportType.equals("png")) {
            PNGExporter exp = new PNGExporter();
            exp.setWorkspace(workspace);
            exp.setWidth(4000);
            exp.setHeight(4000);
            try {
                ec.exportFile(new File("recent.png"), exp);
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        } else if (exportType.equals("pdf")) {
            try {
                ec.exportFile(new File("recent.pdf"));
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
}
