package com.rangel.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameOver;
	Texture[] birds;

	Texture topTube;
	Texture bottomTube;

	int score = 0;
	int scoreingTube = 0;

	//to add text to screen
	BitmapFont font;

	//position is to determine what bird animation to display in render()
	int position = 0;

	//where the bird is currently in the game
	float birdYPosition = 0;
	//how fast the bird will fall
	float velocity = 0;
	//will determine how fast the bird falls as if gravity is pushing it down
	float gravity = 2;
	//gameState determines if the game has began or not
	int gamestate = 0;
	//gap between the tubes
	float gap = 500;
	//the max offset for the tube
	float maxTubeYOffset = 0;

	Random randomGenerator;
	//getting tubes to move across screen
	float tubeVelocity = 4;

	//the amount of tubs that can be on screen at one time
	int numberOfTubes = 4;

	float[] tubeX = new float[numberOfTubes];
	//random offset
	float[] randomOffset = new float[numberOfTubes];

	//distance between tubes as they come on screen
	float distanceBetweenTubes;
	//circle around the bird for colision detection
	Circle birdCircle;
	Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;

	ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover2.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");

		//100 is the "lip" of the tube
		maxTubeYOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		randomGenerator = new Random();

		//shaperenderer to render the shapes for bird and tubes
		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();


		topTubeRectangle = new Rectangle[numberOfTubes];
		bottomTubeRectangle = new Rectangle[numberOfTubes];
		distanceBetweenTubes = Gdx.graphics.getWidth()/2;


		startGame();




	}

	public void startGame(){

		//gets the birds Y axis
		birdYPosition = Gdx.graphics.getHeight()/2 - birds[position].getHeight()/2;

		for(int i = 0; i<numberOfTubes; i++){

			//random generator is between 0 and 1
			randomOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() - topTube.getWidth()/2 + i * distanceBetweenTubes;

			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();


		}
	}

	//this runs continuously
	@Override
	public void render () {

		batch.begin();

		//sets background to height and width of screen
		//note 0,0 is bottom left
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//this is game running
		if(gamestate == 1) {

			//if the next tube has went passed center of the screen
			if(tubeX[scoreingTube] < Gdx.graphics.getWidth()/2){

				score++;

				Gdx.app.log("Score: ", String.valueOf(score));

				//manage the scoring tube
				if(scoreingTube < numberOfTubes - 1){
					scoreingTube++;

				}else{
					scoreingTube = 0;
				}

			}

			//if screen is clicked while game is in play
			if(Gdx.input.justTouched()){

				//how far "up" the bird goes
				velocity = -27;

			}

			for(int i = 0; i < numberOfTubes; i++) {

				//if the tube is off the screen move it back over to the right side of the screen
				if(tubeX[i] < - topTube.getWidth()){
					//how far to move the tubes over
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
				}else{
					tubeX[i] = tubeX[i] - tubeVelocity;



				}


				tubeX[i] = tubeX[i] - tubeVelocity;

				//draw the tubes
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + randomOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + randomOffset[i]);

				topTubeRectangle[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + randomOffset[i], topTube.getWidth(),topTube.getHeight());
				bottomTubeRectangle[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + randomOffset[i], bottomTube.getWidth(),bottomTube.getHeight());

			}



			if(birdYPosition > 0) {

				//sets how fast bird should fall
				velocity = velocity + gravity;
				//updates the birds position
				birdYPosition -= velocity;
			}else{
				gamestate = 2;
			}

			//this is not game running waiting on user to click to begin
		}else if (gamestate == 0){

			if(Gdx.input.justTouched()){
				Gdx.app.log("Touched", "Play game!");
				startGame();
				gamestate = 1;

			}
		}else if (gamestate == 2){

			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);


			if(Gdx.input.justTouched()){
				startGame();
				gamestate = 1;
				score = 0;
				scoreingTube = 0;
				velocity = 0;
			}

		}

		//Code below gets flappy bird to look like he is flapping his little wings
		if(position == 0)
			position = 1;
		else
			position = 0;

		batch.draw(birds[position],Gdx.graphics.getWidth()/2 - birds[position].getWidth()/2, birdYPosition);
		//score text align top middle of screen
		font.draw(batch, String.valueOf(score),Gdx.graphics.getWidth()/2 - birds[0].getWidth()/2/2 , Gdx.graphics.getHeight()/2 + Gdx.graphics.getHeight()/2);
		batch.end();

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);

		birdCircle.set(Gdx.graphics.getWidth()/2,birdYPosition + birds[position].getHeight()/2, birds[position].getWidth()/2);

		for(int i = 0; i<numberOfTubes; i++){

			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + randomOffset[i], topTube.getWidth(),topTube.getHeight());
			//shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + randomOffset[i], bottomTube.getWidth(),bottomTube.getHeight());

			if(Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangle[i])){

				gamestate = 2;

			}

		}

		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);






		shapeRenderer.end();


	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
