package com.example.examcooking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

public class RecipeAdapter extends BaseAdapter {
    private Context context;
    private List<Recipe> recipeList;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return recipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
            holder = new ViewHolder();
            holder.recipeImage = convertView.findViewById(R.id.recipe_image);
            holder.recipeTitle = convertView.findViewById(R.id.recipe_title);
            holder.recipeIngredients = convertView.findViewById(R.id.recipe_ingredients);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Recipe recipe = recipeList.get(position);

        holder.recipeTitle.setText(recipe.getTitle());
        holder.recipeIngredients.setText(recipe.getIngredients());

        // Load image from local storage
        File imgFile = new File(recipe.getImage());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.recipeImage.setImageBitmap(bitmap);
        } else {
            holder.recipeImage.setImageResource(R.drawable.placeholder_image); // Placeholder image
        }

        // Set click listener
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            context.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView recipeImage;
        TextView recipeTitle;
        TextView recipeIngredients;
    }
}