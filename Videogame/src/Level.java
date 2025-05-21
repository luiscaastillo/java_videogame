import java.util.List;

public class Level {
    private String backgroundPath;
    private List<Platform> platforms;

    public Level(String backgroundPath, List<Platform> platforms) {
        this.backgroundPath = backgroundPath;
        this.platforms = platforms;
    }

}