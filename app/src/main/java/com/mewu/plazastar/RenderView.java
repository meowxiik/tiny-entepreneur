package com.mewu.plazastar;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.mewu.plazastar.utils.MapManipulation;
import com.mewu.plazastar.utils.PrecisePoint;
import com.mewu.plazastar.utils.Numbers;
import com.mewu.plazastar.utils.Pair;
import com.mewu.plazastar.utils.Point;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RenderView extends View {

    // Global use variables
    public FullscreenActivity Parent;
    public Matrix CanvasMatrix = new Matrix();
    private Random mRandom = new Random();

    // Store drawing variables
    private static int mDepartmentWidth = 1000 / 2;
    private static int mDepartmentHeight = 300 / 2;

    // Background and ground drawing
    private Rect mGroundSize = new Rect(-400 * 20, -20, 400 * 20, 200);
    private int mBackgroundWidth;
    private int mBackgroundHeight;

    // Array organizers
    int mNextPersonId = 0;
    int mNextMoneyId = 0;

    // Money particle variables
    private final Point mMoneySize = new Point(34 * 2, 15 * 2);
    private SparseArray<String> mMoneyTexts = new SparseArray<>();
    private SparseArray<PrecisePoint> mMoneyPositions = new SparseArray<>();
    private SparseIntArray mMoneyTimeToLive = new SparseIntArray();

    // Person drawing variables
    private final float mElevatorPosition = 0.6f;
    private final int mPeopleScrambleLoops = 10000;
    private final int mPersonAnimationRatio = 2;
    private final Point mPersonSize = new Point((int) (100 * 0.5f) + 10, (int) (210 * 0.5f));

    private int mPeopleSpawnCounter;

    //      Physical person properties
    SparseIntArray mPeopleTypes = new SparseIntArray();
    SparseArray<PrecisePoint> mPeoplePosition = new SparseArray<>();
    SparseArray<Float> mPeopleGoals = new SparseArray<>();
    SparseIntArray mPeopleCounters = new SparseIntArray();
    SparseArray<Float> mPeopleSpeeds = new SparseArray<>();

    //      Person appearance properties
    SparseIntArray mPeopleHeightModifiers = new SparseIntArray();
    SparseBooleanArray mPeopleGoingRight =  new SparseBooleanArray();
    SparseIntArray mPeopleAnimationSteps = new SparseIntArray();

    public RenderView(Context context) {
        super(context);
        setOnTouchListener(new GesturesInterpreter(context, this));
    }

    public RenderView(Context context, AttributeSet attrs){
        super(context, attrs);
        setOnTouchListener(new GesturesInterpreter(context, this));
    }

    public RenderView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setOnTouchListener(new GesturesInterpreter(context, this));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        CanvasMatrix.postTranslate(getWidth() / 2f, getHeight() - mGroundSize.bottom);
        computeBackgroundSize();

        mPeopleSpawnCounter = 0;
        createPerson();

        for (int i = 0; i < mPeopleScrambleLoops; i++)
            UpdatePeople();

        CenterMatrix();
    }

    public void Update(){
        UpdatePeople();
        UpdateMoney();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawBackground(canvas);

        canvas.setMatrix(CanvasMatrix);

        drawGround(canvas);

        drawWalls(canvas);
        drawStore(canvas);

        drawPeople(canvas);

        drawOverlay(canvas);

        if (Parent.BuildMode){
            drawExpansions(canvas);
        }

        drawMoney(canvas);
    }

    //
    //  Background
    //
    private void drawBackground(Canvas canvas){
        Rect r = new Rect();

        int backgroundWidthSpace = mBackgroundWidth - getWidth();

        float[] vals = new float[9];
        CanvasMatrix.getValues(vals);
        float backgroundDx = vals[Matrix.MTRANS_X] / 16f;

        Drawable background = Library.Backgrounds.get(Environment.Day);
        r.top = getHeight() - mBackgroundHeight;
        r.bottom = getHeight();
        r.left = (int) (-backgroundWidthSpace / 2f + backgroundDx);
        r.right = (int) (-backgroundWidthSpace / 2f + mBackgroundWidth + backgroundDx);

        if (r.left > 0){
            float dif = -r.left;
            r.left = 0;
            r.right += dif;
        }

        if (r.right < getWidth()){
            float dif = getWidth() - r.right;
            r.right = getWidth();
            r.left -= dif;
        }

        background.setBounds(r);
        background.draw(canvas);
    }

    private void drawGround(Canvas canvas){

        Rect r = new Rect();

        Drawable ground = Library.Grounds.get(Environment.Day);

        r.top = mGroundSize.top;
        r.bottom = mGroundSize.bottom;
        r.left = mGroundSize.left;
        r.right = mGroundSize.right;

        ground.setBounds(mGroundSize);
        ground.draw(canvas);
    }

    //
    //  Store drawing
    //
    private void drawStore(Canvas canvas){
        Rect r = new Rect();
        for(Map.Entry<Point, Integer> entry : GameState.Instance.HotelMap.entrySet()) {
            int x = entry.getKey().X;
            int y = entry.getKey().Y;
            int id = entry.getValue();
            Drawable texture = Library.Textures.get(id);

            if (texture == null){
                //Log.println(Log.ERROR, "Plaza", "Could not find texture with Id: " + id);
                continue;
            }


            r.left = mDepartmentWidth * x;
            r.top = mDepartmentHeight * -(y + 1);
            r.right = r.left + mDepartmentWidth;
            r.bottom = r.top + mDepartmentHeight;
            texture.setBounds(r);
            texture.draw(canvas);
        }
    }

    private void drawWalls(Canvas canvas){
        Rect r = new Rect();

        Drawable wall = Library.Wall;
        Drawable roof = Library.Roof;
        for(Map.Entry<Point, Integer> entry : GameState.Instance.HotelMap.entrySet()) {
            int id = entry.getValue();
            if (!Library.IsBuilding(id))
                continue;

            int x = entry.getKey().X;
            int y = entry.getKey().Y;
            Point topRoom = new Point(x, y + 1);
            Point rightRoom = new Point(x + 1, y);

            r.left = mDepartmentWidth * x;
            r.top = mDepartmentHeight * -(y + 1) - 20;
            r.right = r.left + mDepartmentWidth + 20;
            r.bottom = r.top + mDepartmentHeight + 20;

            if (!GameState.Instance.HotelMap.containsKey(topRoom) || !Library.IsBuilding(GameState.Instance.HotelMap.get(topRoom))){
                roof.setBounds(r);
                roof.draw(canvas);
            }

            if (!GameState.Instance.HotelMap.containsKey(rightRoom) || !Library.IsBuilding(GameState.Instance.HotelMap.get(rightRoom))){
                wall.setBounds(r);
                wall.draw(canvas);
            }
        }
    }

    private void drawOverlay(Canvas canvas){
        Rect r = new Rect();
        for(Map.Entry<Point, Integer> entry : GameState.Instance.HotelMap.entrySet()) {
            int x = entry.getKey().X;
            int y = entry.getKey().Y;
            int id = entry.getValue();

            if (!Library.IsBuilding(id))
                continue;

            Point leftRoom = new Point(x - 1, y);
            Point rightRoom = new Point(x + 1, y);

            boolean leftConnectable = GameState.Instance.HotelMap.containsKey(leftRoom) && Library.IsConnectable(GameState.Instance.HotelMap.get(leftRoom));
            boolean rightConnectable = GameState.Instance.HotelMap.containsKey(rightRoom) && Library.IsConnectable(GameState.Instance.HotelMap.get(rightRoom));

            Drawable overlay;

            if (!Library.IsConnectable(id)){
                overlay = Library.Overlay;
            }
            else if (leftConnectable && rightConnectable){
                overlay = Library.OverlayBothOpen;
            }
            else if (leftConnectable){
                overlay = Library.OverlayLeftOpen;
            }
            else if (rightConnectable){
                overlay = Library.OverlayRightOpen;
            }
            else {
                overlay = Library.Overlay;
            }

            r.left = mDepartmentWidth * x;
            r.top = mDepartmentHeight * -(y + 1);
            r.right = r.left + mDepartmentWidth;
            r.bottom = r.top + mDepartmentHeight;

            overlay.setBounds(r);
            overlay.draw(canvas);
        }
    }

    private void drawExpansions(Canvas canvas){
        Rect r = new Rect();
        Paint border = new Paint();
        border.setColor(Color.WHITE);

        Drawable gradient = Library.Gradient;
        for (Point expansion : GameState.Instance.GetExpansions()){
            int x = expansion.X;
            int y = expansion.Y;

            r.left = mDepartmentWidth * x;
            r.top = mDepartmentHeight * -(y + 1);
            r.right = r.left + mDepartmentWidth;
            r.bottom = r.top + mDepartmentHeight;

            gradient.setBounds(r);
            gradient.draw(canvas);
        }

        Drawable deconstruction = Library.Deconstruction;
        for(Map.Entry<Point, Integer> entry : GameState.Instance.HotelMap.entrySet()) {
            int x = entry.getKey().X;
            int y = entry.getKey().Y;
            int id = entry.getValue();

            if (id == Library.IDConstruction)
                continue;

            r.left = mDepartmentWidth * x;
            r.top = mDepartmentHeight * -(y + 1);
            r.right = r.left + mDepartmentWidth;
            r.bottom = r.top + mDepartmentHeight;
            deconstruction.setBounds(r);
            deconstruction.draw(canvas);
        }

        Drawable sign = Library.Sign;
        Pair<Integer, Integer> edges = GameState.Instance.Owned;
        int x;
        int y = 0;

        x = edges.P1 - 1;
        r.left = mDepartmentWidth * x;
        r.top = mDepartmentHeight * -(y + 1);
        r.right = r.left + mDepartmentWidth;
        r.bottom = r.top + mDepartmentHeight;
        sign.setBounds(r);
        sign.draw(canvas);

        x = edges.P2 + 1;
        r.left = mDepartmentWidth * x;
        r.top = mDepartmentHeight * -(y + 1);
        r.right = r.left + mDepartmentWidth;
        r.bottom = r.top + mDepartmentHeight;
        sign.setBounds(r);
        sign.draw(canvas);

    }

    //
    //  Money particle drawing
    //
    private void drawMoney(Canvas canvas){

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        p.setTextAlign(Paint.Align.RIGHT);
        p.setTextSize(mMoneySize.Y);

        Drawable money = Library.Money;
        Rect r = new Rect();

        for(int i = 0; i < mMoneyTexts.size(); i++) {
            int key = mMoneyTexts.keyAt(i);

            String text = mMoneyTexts.get(key);
            PrecisePoint position = mMoneyPositions.get(key);

            canvas.drawText(text, position.X, -position.Y, p);

            r.left = (int) position.X;
            r.top = (int) -position.Y - mMoneySize.Y;
            r.right = r.left + mMoneySize.X;
            r.bottom = r.top + mMoneySize.Y;

            money.setBounds(r);
            money.draw(canvas);
        }
    }

    public void UpdateMoney(){
        for(int i = 0; i < mMoneyTexts.size(); i++) {
            int key = mMoneyTexts.keyAt(i);

            PrecisePoint position = mMoneyPositions.get(key);
            int timeToLive = mMoneyTimeToLive.get(key);
            timeToLive--;
            position.Y+= 3f;

            if (timeToLive <= 0)
                discardMoneyParticle(key);

            mMoneyTimeToLive.put(key, timeToLive);
        }
    }

    private void createMoneyParticle(float x, float y){

        x += (mRandom.nextFloat() - 0.5f) * mDepartmentWidth;
        y += (mRandom.nextFloat() - 0.5f) * mDepartmentHeight;

        createMoneyParticle(x, y, (long) GameState.Instance.IncomePerTap());
    }

    private void createMoneyParticle(float x, float y, long money){
        mMoneyTexts.put(mNextMoneyId, Numbers.HumanizeNumber(money) + " ");
        mMoneyPositions.put(mNextMoneyId, new PrecisePoint(x, y));
        mMoneyTimeToLive.put(mNextMoneyId, 20);
        mNextMoneyId++;
    }

    public void createRandomMoney(float money){
        Point room = MapManipulation.GetRandomRoom(mRandom);
        createMoneyParticle(((float)room.X + mRandom.nextFloat()) * mDepartmentWidth, ((float)room.Y + mRandom.nextFloat()) * mDepartmentHeight, (long) money);
    }

    private void discardMoneyParticle(int key){
        mMoneyTexts.remove(key);
    }

    //
    // People drawing
    //
    private void drawPeople(Canvas canvas) {
        Rect r = new Rect();

        Paint debug = new Paint();
        debug.setColor(Color.RED);

        for(int i = 0; i < mPeopleTypes.size(); i++) {
            int key = mPeopleTypes.keyAt(i);

            int type = mPeopleTypes.get(key);
            int animationStep = mPeopleAnimationSteps.get(key) / mPersonAnimationRatio;

            int height = Library.PeopleSizes.get(type) + mPeopleHeightModifiers.get(key) + mPersonSize.Y;
            int width = height / mPersonSize.Y * mPersonSize.X;

            boolean mirrored = mPeopleGoingRight.get(key);
            PrecisePoint position = mPeoplePosition.get(key);

            Drawable texture = Library.People.get(type)[animationStep];

            r.left = (int) (mDepartmentWidth * position.X);
            r.top = (int) (mDepartmentHeight * -position.Y) - height;
            r.right = r.left + width;
            r.bottom = r.top + height;

            texture.setBounds(r);

            if (mirrored){
                CanvasMatrix.preScale(-1, 1,  r.centerX(), r.centerY());
            }

            canvas.setMatrix(CanvasMatrix);
            texture.draw(canvas);

            if (mirrored)
                CanvasMatrix.preScale(-1, 1, r.centerX(), r.centerY());

            canvas.setMatrix(CanvasMatrix);

            // This part draws peoples goal
            //r.left = (int) (mPeopleGoals.get(key) * mDepartmentWidth);
            //r.right = r.left + 5;
            //canvas.drawRect(r, debug);
        }
    }

    public void UpdatePeople(){

        if (mPeopleTypes.size() < GameState.Instance.MapSize * 10 + 20 && mRandom.nextInt(10) == 0)
            createPerson();

        for(int i = 0; i < mPeopleTypes.size(); i++) {
            int key = mPeopleTypes.keyAt(i);

            PrecisePoint position = mPeoplePosition.get(key);
            float goal = mPeopleGoals.get(key);

            if (Math.abs(position.X - goal) < 0.05f){
                int counter = mPeopleCounters.get(key);
                counter--;
                mPeopleCounters.put(key, counter);

                if (counter == 1){
                    if (position.Y == 0)
                        goal = 0;
                    else
                        goal = (float) (Math.floor(goal) + mElevatorPosition);
                }
                else if (counter == 0){
                    discardPerson(i);
                    continue;
                }
                else {
                    goal = (float) (Math.floor(goal) + Math.abs(mRandom.nextFloat() - 0.2f)); // the 0.2 is there to make sure people dont go too far to the edge, causing them to overlap to next cell
                }

                mPeopleGoals.put(key, goal);
                mPeopleGoingRight.put(key, position.X < goal);
            }

            float speed = mPeopleSpeeds.get(key);
            if (position.X < goal)
                position.X += speed;
            else
                position.X -= speed;

           int step = mPeopleAnimationSteps.get(key);
           step++;
           if (step >= 4 * mPersonAnimationRatio)
               step = 0;
           mPeopleAnimationSteps.put(key, step);
        }
    }

    private void createPerson(){
        int type = mRandom.nextInt(Library.People.size());

        PrecisePoint position = new PrecisePoint(0, 0);
        float goal = 0;
        int numGoals = 0;

        switch (mPeopleSpawnCounter){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                List<Point> spawnables = MapManipulation.GetSpawnableRooms();
                if (spawnables.size() != 0){
                    Point spawn = spawnables.get(mRandom.nextInt(spawnables.size()));

                    position = new PrecisePoint((float)((double)(spawn.X) + (double)mElevatorPosition), spawn.Y);
                    goal = spawn.X + Math.abs(mRandom.nextFloat() - 0.2f);
                    numGoals = 4;
                    break;
                }
            case 6:
            case 7:
            case 8:
            case 9:
                // Code for people to just walk from one side of the screen to the other side
                Pair<Integer, Integer> edges = GameState.Instance.Owned;

                float x = mPeopleSpawnCounter % 2 == 0 ? edges.P1 - 2 : edges.P2 + 2;
                goal = mPeopleSpawnCounter % 2 == 0 ? edges.P2 + 2 : edges.P1 - 2;
                position = new PrecisePoint(x, 0);
                numGoals = 1;
                break;
        }


        mPeopleTypes.put(mNextPersonId, type);
        mPeoplePosition.put(mNextPersonId, position);
        mPeopleGoals.put(mNextPersonId, goal);
        mPeopleCounters.put(mNextPersonId, numGoals);
        mPeopleSpeeds.put(mNextPersonId, 0.01f * ((mRandom.nextFloat() * 2f) + 1f) / 2f);

        mPeopleAnimationSteps.put(mNextPersonId, 0);
        mPeopleGoingRight.put(mNextPersonId, position.X < goal);
        mPeopleHeightModifiers.put(mNextPersonId, mRandom.nextInt(20) - 10);

        mNextPersonId++;
        mPeopleSpawnCounter++;
        if (mPeopleSpawnCounter >= 10)
            mPeopleSpawnCounter = 0;
    }

    private void discardPerson(int position){
        mPeopleTypes.removeAt(position);
    }

    //
    //  Utility methods
    //
    public void ProcessClick(float x, float y){
        Point destination = InverseLocation(x, y);

        if (!Parent.BuildMode){
            if (GameState.Instance.HotelMap.containsKey(destination)){
                GameState.Instance.Earn();
                PrecisePoint tapLocation = InverseLocationPrecise(x, y);
                createMoneyParticle(tapLocation.X, tapLocation.Y);
            }
        }

        if (Parent.BuildMode){

            if (GameState.Instance.GetExpansions().contains(destination)) {
                Parent.OpenDepartmentStore(destination.X, destination.Y);
            }

            if (GameState.Instance.HotelMap.containsKey(destination) && GameState.Instance.HotelMap.get(destination) != Library.IDConstruction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to remove this " + Library.Names.get(GameState.Instance.HotelMap.get(destination)) + " ?");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {GameState.Instance.Deconstruct(destination);
                    Parent.StopShopping();});
                builder.setNegativeButton("No", (dialogInterface, i) -> Parent.StopShopping());
                builder.create();
                builder.show();
            }

            if (destination.Y == 0){
                Pair<Integer, Integer> owned = GameState.Instance.Owned;
                int purchaseable1 = owned.P1 - 1;
                int purchaseable2 = owned.P2 + 1;

                if (destination.X == purchaseable1 || destination.X == purchaseable2) {

                    long price = GameState.Instance.GetPropertyExpansionPrice(destination.X);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    if (GameState.Instance.Money >= price){
                        builder.setMessage("Do you want to buy this property for  " + GameState.Instance.GetPropertyExpansionPrice(destination.X) + " ?");
                        builder.setPositiveButton("Yes", (dialogInterface, i) -> {GameState.Instance.BuyProperty(destination.X);
                            Parent.StopShopping();});
                        builder.setNegativeButton("No", (dialogInterface, i) -> Parent.StopShopping());
                    }
                    else {
                        builder.setMessage("You cannot afford this property!");
                        builder.setPositiveButton("OK :(", (dialogInterface, i) -> Parent.StopShopping());
                    }

                    builder.create();
                    builder.show();
                }
            }
        }
    }

    private Point InverseLocation(float x, float y){
        float[] origin = new float[2];

        origin[0] = x;
        origin[1] = y;

        Matrix inverse = new Matrix();
        if(CanvasMatrix.invert(inverse)){
            inverse.mapPoints(origin);
        }

        x = origin[0];
        y = origin[1];

        x /= mDepartmentWidth;
        y /= mDepartmentHeight;

        x = (int) Math.floor(x);
        y = (int) Math.floor(y);

        y = -(y + 1);

        return new Point((int)x, (int)y);
    }

    private PrecisePoint InverseLocationPrecise(float x, float y){
        float[] origin = new float[2];

        origin[0] = x;
        origin[1] = y;

        Matrix inverse = new Matrix();
        if(CanvasMatrix.invert(inverse)){
            inverse.mapPoints(origin);
        }

        x = origin[0];
        y = origin[1];

        y = -y;

        return new PrecisePoint(x, y);
    }


    public void RestrictMatrix() {
        Pair<Integer, Integer> edges = GameState.Instance.Owned;

        int edgeA = Math.abs(edges.P2);
        int edgeB = Math.abs(edges.P1);

        if (edges.P1 < 0)
            edgeA = -edgeA;
        if (edges.P2 < 0)
            edgeB = -edgeB;


        float[] values = new float[9];
        CanvasMatrix.getValues(values);

        float maxScale = 1.5f;
        float minScale = 0.2f;

        float scale = values[Matrix.MSCALE_X];

        if (scale > maxScale){
            scale = maxScale;
        }

        if (scale < minScale){
            scale = minScale;
        }

        values[Matrix.MSCALE_X] = scale;
        values[Matrix.MSCALE_Y] = scale;

        int maxX = (int) (edgeB * mDepartmentWidth + mDepartmentWidth + 500 * scale);
        int minX = (int) (edgeA * mDepartmentWidth - 500 * scale);

        int maxY = (int) (GameState.Instance.Tallest * mDepartmentHeight + 500 * scale);
        int minY = getHeight() - mGroundSize.bottom;


        if (values[Matrix.MTRANS_X] > maxX){
            values[Matrix.MTRANS_X] = maxX;
        }

        if (values[Matrix.MTRANS_X] < minX){
            values[Matrix.MTRANS_X] = minX;
        }

        if (values[Matrix.MTRANS_Y] > maxY){
            values[Matrix.MTRANS_Y] = maxY;
        }

        if (values[Matrix.MTRANS_Y] < minY){
            values[Matrix.MTRANS_Y] = minY;
        }



        CanvasMatrix.setValues(values);
    }

    public void CenterMatrix(){
        Pair<Integer, Integer> edges = GameState.Instance.Owned;

        int edgeA = Math.abs(edges.P2);
        int edgeB = Math.abs(edges.P1);

        if (edges.P1 < 0)
            edgeA = -edgeA;
        if (edges.P2 < 0)
            edgeB = -edgeB;


        float[] values = new float[9];
        CanvasMatrix.getValues(values);

        float maxScale = 1.5f;
        float minScale = 0.2f;

        float scale = values[Matrix.MSCALE_X];

        if (scale > maxScale){
            scale = maxScale;
        }

        if (scale < minScale){
            scale = minScale;
        }

        values[Matrix.MSCALE_X] = scale;
        values[Matrix.MSCALE_Y] = scale;

        int maxX = (int) (edgeB * mDepartmentWidth + mDepartmentWidth + 500 * scale);
        int minX = (int) (edgeA * mDepartmentWidth - 500 * scale);

        int minY = getHeight() - mGroundSize.bottom;

        values[Matrix.MTRANS_X] = (maxX + minX) / 2f;
        values[Matrix.MTRANS_Y] = minY;

        CanvasMatrix.setValues(values);
    }

    private void computeBackgroundSize(){
        int destinationWidth = getWidth() + 400;
        int destinationHeight = getHeight();

        int ratio_x = Library.BackgroundSize.X;
        int ratio_y = Library.BackgroundSize.Y;

        float scale_x = destinationWidth / (float) ratio_x;
        float scale_y = destinationHeight / (float) ratio_y;

        float scale;

        if (scale_x > scale_y)
            scale = scale_x;
        else
            scale = scale_y;

        mBackgroundWidth = (int) (ratio_x * scale);
        mBackgroundHeight = (int) (ratio_y * scale);
    }

    private class GesturesInterpreter implements OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
        private ScaleGestureDetector gestureScale;
        private RenderView render;

        private float prevX;
        private float prevY;
        private boolean moved = false;
        private boolean zooming = false;

        public GesturesInterpreter(Context c, RenderView render){
            gestureScale = new ScaleGestureDetector(c, this);
            this.render = render;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            gestureScale.onTouchEvent(event);

            if (event.getPointerCount() == 1 && !zooming)
                moveGesture(event, event.getX(0), event.getY(0));
            else if (event.getPointerCount() == 1) {
                zooming = false;
            } else {
                zooming = true;
            }

            prevX = event.getX(0);
            prevY = event.getY(0);


            RestrictMatrix();
            render.invalidate();

            return true;
        }

        private void moveGesture(MotionEvent event, float x, float y){
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                moved = false;
                prevX = x;
                prevY = y;
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE){

                float deltaX = prevX - x;
                float deltaY = prevY - y;

                if (deltaX > 1 || deltaY > 1)
                    moved = true;

                render.CanvasMatrix.postTranslate(-deltaX, -deltaY);

                prevX = x;
                prevY = y;

            } else if (event.getAction() == MotionEvent.ACTION_UP){
                if (!moved)
                    ProcessClick(x, y);
            }
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            render.CanvasMatrix.postScale(scaleFactor, scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }
}
