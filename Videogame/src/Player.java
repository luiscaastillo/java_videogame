public class Player extends Entity {
//
//    KeyHandler keyH = new KeyHandler();
//
//    public Player(double gravity) {
//        this.x = 100;
//        this.y = 500;
//        this.speed = 4;
//        this.gravity = .5;
//    }
//
//    private final double gravity;
//
//    public void update() {
//        int floor = screen_height - 75;
//
//        if (keyH.upPressed && this.y + tile_size >= floor) {
//            this.speed = -10;
//        }
//
//        this.speed += (int) gravity;
//        this.y += this.speed;
//
//        if (keyH.downPressed) {
//            this.y += player_speed;
//            if (this.y + tile_size > floor) {
//                this.y = floor - tile_size;
//                this.speed = 0;
//            }
//        }
//        if (keyH.leftPressed) {
//            this.x -= player_speed;
//            if (this.x < 0) {
//                this.x = 0;
//            }
//        }
//        if (keyH.rightPressed) {
//            this.x += player_speed;
//            if (this.x + tile_size > screen_width) {
//                this.x = screen_width - tile_size;
//            }
//        }
//        if (this.y + tile_size > floor) {
//            this.y = floor - tile_size;
//            this.speed = 0;
//        }
//    }
}
