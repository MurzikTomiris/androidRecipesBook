package com.example.examcooking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AddEditRecipeActivity extends AppCompatActivity {
    private Button selectImageButton;
    private Button saveButton;
    private Button backButton;
    private EditText titleEditText;
    private EditText ingredientsEditText;
    private EditText stepsEditText;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private RecipeDatabaseHelper dbHelper;
    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectImageButton = findViewById(R.id.select_image_button);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        titleEditText = findViewById(R.id.title_edit_text);
        ingredientsEditText = findViewById(R.id.ingredients_edit_text);
        stepsEditText = findViewById(R.id.steps_edit_text);
        dbHelper = new RecipeDatabaseHelper(this);

        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("RECIPE_ID");

        // Если ID рецепта существует, загружаем рецепт для редактирования
        if (recipeId != null) {
            loadRecipeDetails(recipeId);
        }



        selectImageButton.setOnClickListener(v -> selectImage());
        saveButton.setOnClickListener(v -> saveRecipe());

        backButton.setOnClickListener(view -> {
            Intent intentBack = new Intent(AddEditRecipeActivity.this, MainActivity.class);
            startActivity(intentBack);
        });
    }

    private void loadRecipeDetails(String recipeId) {
        currentRecipe = dbHelper.getRecipe(recipeId);

        if (currentRecipe != null) {
            titleEditText.setText(currentRecipe.getTitle());
            ingredientsEditText.setText(currentRecipe.getIngredients());
            stepsEditText.setText(currentRecipe.getSteps());
        }
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    public void saveRecipe() {
        String title = titleEditText.getText().toString();
        String ingredients = ingredientsEditText.getText().toString();
        String steps = stepsEditText.getText().toString();

        String id = currentRecipe != null ? currentRecipe.getId() : UUID.randomUUID().toString();

        String imagePath = saveImageToLocalStorage(imageUri);



        if (currentRecipe != null) {
            String imageString = currentRecipe.getImage();
            Recipe recipe = new Recipe(id, title, imageString, ingredients, steps);
            dbHelper.updateRecipe(recipe);
        } else {
            Recipe recipe = new Recipe(id, title, imagePath, ingredients, steps);
            dbHelper.addRecipe(recipe);
        }
        finish();
    }

    private String saveImageToLocalStorage(Uri imageUri) {
        String imagePath = null;

        if (imageUri ==null){
            imagePath = "android.resource://" + getPackageName() + "/" + R.drawable.placeholder_image;
            return imagePath;
        }


        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File directory = new File(getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String uniqueFileName = "recipe_image_" + UUID.randomUUID().toString() + ".jpg";

            File imageFile = new File(directory, uniqueFileName);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            imagePath = imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            imagePath = "android.resource://" + getPackageName() + "/" + R.drawable.placeholder_image;
        }
        return imagePath;
    }
}