package com.example.examcooking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class RecipeDetailsActivity extends AppCompatActivity {
    private RecipeDatabaseHelper dbHelper;
    private Recipe currentRecipe;

    private ImageView recipeImageView;
    private TextView titleTextView;
    private TextView ingredientsTextView;
    private TextView stepsTextView;
    private Button editButton;
    private Button deleteButton;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new RecipeDatabaseHelper(this);

        recipeImageView = findViewById(R.id.recipe_image_view);
        titleTextView = findViewById(R.id.title_text_view);
        ingredientsTextView = findViewById(R.id.ingredients_text_view);
        stepsTextView = findViewById(R.id.steps_text_view);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);


        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("RECIPE_ID");
        loadRecipeDetails(recipeId);

        editButton.setOnClickListener(view -> openEditRecipe());
        deleteButton.setOnClickListener(view -> confirmDeleteRecipe());
        backButton.setOnClickListener(view -> {
            Intent intentBack = new Intent(RecipeDetailsActivity.this, MainActivity.class);
            startActivity(intentBack);
        });
    }

    private void loadRecipeDetails(String recipeId) {
        currentRecipe = dbHelper.getRecipe(recipeId);
        if (currentRecipe == null) {
            Toast.makeText(this, "Рецепт не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleTextView.setText(currentRecipe.getTitle());
        ingredientsTextView.setText(currentRecipe.getIngredients());
        stepsTextView.setText(currentRecipe.getSteps());

        // Загрузка изображения из пути
        File imgFile = new File(currentRecipe.getImage());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            recipeImageView.setImageBitmap(bitmap);
        } else {
            recipeImageView.setImageResource(R.drawable.placeholder_image); // Плейсхолдер для отсутствующего изображения
        }
    }

    private void openEditRecipe() {
        Intent intent = new Intent(this, AddEditRecipeActivity.class);
        intent.putExtra("RECIPE_ID", currentRecipe.getId());
        startActivity(intent);
    }

    private void confirmDeleteRecipe() {
        new AlertDialog.Builder(this)
                .setTitle("Удаление рецепта")
                .setMessage("Вы уверены, что хотите удалить этот рецепт?")
                .setPositiveButton("Удалить", (dialog, which) -> deleteRecipe())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void deleteRecipe() {
        dbHelper.deleteRecipe(currentRecipe.getId()); // Вызов без условия
        Toast.makeText(this, "Рецепт удалён", Toast.LENGTH_SHORT).show();
        finish(); // Завершаем активность после удаления
    }
}