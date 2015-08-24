package ludumdare._33.world.cat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ludumdare._33.assets.AnimationTextures;
import ludumdare._33.world.World;
import ludumdare._33.world.environment.Platforms;

public class Cat {
	
	public int height = 48;
	public int width = 69;

	Animation sittingAnimation;
	Animation runningAnimation;
	Animation jumpingAnimation;
	
	Animation sittingFoodAnimation;
	Animation runningFoodAnimation;
	Animation jumpingFoodAnimation;
	
	Animation currentAnimation;
	float currentAnimationTime;
	Vector2 position;
	Rectangle floorCheck = new Rectangle();
	
	CatState previousState = CatState.Sitting;
	CatState currentState = CatState.Sitting;
	public boolean hasFood = true;
	boolean facingRight = true;
	boolean onFloorOrPlatform;
	
	Vector2 displacement = new Vector2();
	float velocityY;
	
	public Cat() {
		position = new Vector2(100, 0);
		initialiseAnimations();
		currentAnimation = sittingAnimation;
	}

	public void update(float delta) {
		currentAnimationTime += delta;
		updateFloorCheckBounds();
		onFloorOrPlatform = currentlyOnFloorOrPlatform();
		manageInput();
		updateVelocityY(delta);
		updateDisplacement();
		updateState();
		clampToWorldBounds();
		displacement.x = 0;
		displacement.y = 0;
	}
	
	public Vector2 getCatPosition() {
		return new Vector2(position).cpy().add(width / 2, height / 2);
	}

	public void draw(SpriteBatch batch) {
		TextureRegion cat = currentAnimation.getKeyFrame(currentAnimationTime);
		batch.draw(cat, facingRight?position.x+width:position.x, position.y,facingRight?-width:width,height);
	}

	public void manageInput() {

		if (Gdx.input.isKeyPressed(Keys.W)) {
			if(onFloorOrPlatform){
				velocityY = 700;
			}else{
				displacement.y += 4;
			}
		}
		
		if (Gdx.input.isKeyPressed(Keys.S)) {
			if(onFloorOrPlatform){
				displacement.y -= 4;
			}
		}

		if (Gdx.input.isKeyPressed(Keys.D)) {
			displacement.x += 7;
		} else if (Gdx.input.isKeyPressed(Keys.A)) {
			displacement.x -= 7;
		}
		
	}
	
	void updateVelocityY(float delta) {
		if(onFloorOrPlatform && velocityY < 0){
			velocityY = 0;
		}else{
			velocityY -= delta * 1200;
		}
		if(!onFloorOrPlatform || velocityY > 0){
			displacement.y = velocityY * delta;
		}
	}
	
	void updateDisplacement(){
		if(displacement.x > 0){
			facingRight = true;
		}else if(displacement.x < 0){
			facingRight = false;
		}
		position.x += displacement.x;
		position.y += displacement.y;
	}

	public void updateState(){
		if(!onFloorOrPlatform){
			changeState(CatState.Jumping);
		}else if(displacement.x != 0){
			changeState(CatState.Running);
		}else{
			changeState(CatState.Sitting);
		}
		
		if(previousState != currentState){
			currentAnimationTime = 0;
		}
		if (hasFood) {
			switch (currentState) {
			case Sitting:
				currentAnimation = sittingFoodAnimation;
				break;
			case Running:
				currentAnimation = runningFoodAnimation;
				break;
			case Jumping:
				currentAnimation = jumpingFoodAnimation;
				break;
			}
		} else {
			switch (currentState) {
			case Sitting:
				currentAnimation = sittingAnimation;
				break;
			case Running:
				currentAnimation = runningAnimation;
				break;
			case Jumping:
				currentAnimation = jumpingAnimation;
				break;
			}
		}
	}
	
	void clampToWorldBounds(){
		position.x = MathUtils.clamp(position.x, World.bounds.x, World.bounds.width - width);
		position.y = MathUtils.clamp(position.y, World.bounds.y, World.bounds.height - height);
	}
	
	public boolean currentlyOnFloorOrPlatform() {
		if (position.y == 0 || Platforms.collidingWithPlatform(floorCheck)){
			return true;
		}
		return false;
	}

	void changeState(CatState state){
		previousState = currentState;
		currentState = state;
	}

	void initialiseAnimations() {
		sittingAnimation = new Animation(0.5f, AnimationTextures.catSittingArray.toArray(new TextureRegion[AnimationTextures.catSittingArray.size()]));
		sittingAnimation.setPlayMode(PlayMode.LOOP);
		runningAnimation = new Animation(0.05f, AnimationTextures.catRunningArray.toArray(new TextureRegion[AnimationTextures.catRunningArray.size()]));
		runningAnimation.setPlayMode(PlayMode.LOOP);
		jumpingAnimation = new Animation(0.4f, AnimationTextures.catJumpingArray.toArray(new TextureRegion[AnimationTextures.catJumpingArray.size()]));
		jumpingAnimation.setPlayMode(PlayMode.LOOP);
		
		sittingFoodAnimation = new Animation(0.5f, AnimationTextures.catSittingFoodArray.toArray(new TextureRegion[AnimationTextures.catSittingFoodArray.size()]));
		sittingFoodAnimation.setPlayMode(PlayMode.LOOP);
		runningFoodAnimation = new Animation(0.05f, AnimationTextures.catRunningFoodArray.toArray(new TextureRegion[AnimationTextures.catRunningFoodArray.size()]));
		runningFoodAnimation.setPlayMode(PlayMode.LOOP);
		jumpingFoodAnimation = new Animation(0.4f, AnimationTextures.catJumpingFoodArray.toArray(new TextureRegion[AnimationTextures.catJumpingFoodArray.size()]));
		jumpingFoodAnimation.setPlayMode(PlayMode.LOOP);
	}
	
	void updateFloorCheckBounds(){
		floorCheck.x = position.x + width * 0.35f;
		floorCheck.y = position.y;
		floorCheck.width = width * 0.3f;
		floorCheck.height = 2;
	}
	
	public void drawFloorCheck(ShapeRenderer shapeRenderer){
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(floorCheck.x, floorCheck.y, floorCheck.width, floorCheck.height);
		shapeRenderer.end();
	}
}
