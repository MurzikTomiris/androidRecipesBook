package com.example.examcooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RECIPES = "recipes";

    // Поля таблицы
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_STEPS = "steps";

    public RecipeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECIPES_TABLE = "CREATE TABLE " + TABLE_RECIPES + " ("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_INGREDIENTS + " TEXT, "
                + COLUMN_STEPS + " TEXT)";
        db.execSQL(CREATE_RECIPES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    public void addRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, recipe.getId());
        values.put(COLUMN_TITLE, recipe.getTitle());
        values.put(COLUMN_IMAGE, recipe.getImage());
        values.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        values.put(COLUMN_STEPS, recipe.getSteps());

        db.insert(TABLE_RECIPES, null, values);
        db.close();
    }

    public Recipe getRecipe(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_IMAGE, COLUMN_INGREDIENTS, COLUMN_STEPS},
                COLUMN_ID + "=?", new String[]{id}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Recipe recipe = new Recipe(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            cursor.close();
            return recipe;
        }
        return null;
    }

    public List<Recipe> searchRecipesByName(String title) {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RECIPES + " WHERE " + COLUMN_TITLE + " LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + title + "%"});

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STEPS))
                );
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return recipes;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STEPS))
                );
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    public void deleteRecipe(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECIPES, "id = ?", new String[]{id});
    }

    public void updateRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, recipe.getTitle());
        values.put(COLUMN_IMAGE, recipe.getImage());
        values.put(COLUMN_INGREDIENTS, recipe.getIngredients());
        values.put(COLUMN_STEPS, recipe.getSteps());

        db.update(TABLE_RECIPES, values, COLUMN_ID + "=?", new String[]{recipe.getId()});
        db.close();
    }
}
