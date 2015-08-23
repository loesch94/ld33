package ludumdare._33.world.environment.buildings;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ludumdare._33.Assets.StaticTextures;

public class Bungalow1 extends Building {
	private static ArrayList<Rectangle> platforms;
	private static Vector2 size = new Vector2(350, 250);

	static {
		platforms = new ArrayList<Rectangle>();
		platforms.add(new Rectangle(size.x / 64f, size.y / 2f, size.x / 1.06f, size.y / 2.8f));
	}

	public Bungalow1(Vector2 position) {
		super(StaticTextures.Bungalow1, position, size, platforms);
	}
}