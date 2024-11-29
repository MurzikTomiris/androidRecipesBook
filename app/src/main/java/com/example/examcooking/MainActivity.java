package com.example.examcooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText searchField;
    private Button searchButton, addButton;
    private ListView recipeListView;
    private RecipeAdapter recipeAdapter;
    private RecipeDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchField = findViewById(R.id.et_search);
        searchButton = findViewById(R.id.btn_search);
        addButton = findViewById(R.id.btn_add);
        recipeListView = findViewById(R.id.lv_recipes);

        dbHelper = new RecipeDatabaseHelper(this);

        // Загрузка всех рецептов
        loadRecipes();

        // Поиск рецептов
        searchButton.setOnClickListener(view -> searchRecipes());

        // Переход к экрану добавления рецепта
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditRecipeActivity.class);
            startActivity(intent);
        });
    }

    private void loadRecipes() {
        List<Recipe> recipeList = dbHelper.getAllRecipes();
        recipeAdapter = new RecipeAdapter(this, recipeList);
        recipeListView.setAdapter(recipeAdapter);
    }

    public void searchRecipes() {
        String query = searchField.getText().toString().trim();

        if (query.isEmpty()) {
            loadRecipes(); // Если поле поиска пустое, загружаем все рецепты
            return;
        }

        List<Recipe> searchResults = dbHelper.searchRecipesByName(query);
        RecipeAdapter recipeAdapter = new RecipeAdapter(this, searchResults);
        recipeListView.setAdapter(recipeAdapter);

        if (searchResults.isEmpty()) {
            Toast.makeText(this, "Рецепт не найден", Toast.LENGTH_SHORT).show();
        }
    }
}