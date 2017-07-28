package com.mihailproductions.inventory.Adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mihailproductions.inventory.data.ItemContract.ItemEntry;
import com.mihailproductions.inventory.R;
public class ItemCursorAdapter extends CursorAdapter {
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view,final Context context, final Cursor cursor) {
        final Uri uri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, cursor.getInt(cursor.getColumnIndexOrThrow(ItemEntry._ID)));
        ImageView itemImageIV = (ImageView) view.findViewById(R.id.itemImage);
        ImageView saleIV =(ImageView) view.findViewById(R.id.sale);
        TextView itemNameTV = (TextView) view.findViewById(R.id.itemName);
        TextView itemStockTV = (TextView) view.findViewById(R.id.itemStock);
        TextView itemPriceTV = (TextView) view.findViewById(R.id.itemPrice);

        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int stockColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE);

        itemNameTV.setText(cursor.getString(nameColumnIndex));
        itemStockTV.setText(cursor.getString(stockColumnIndex));
        itemPriceTV.setText(cursor.getString(priceColumnIndex));
        itemImageIV.setImageURI(Uri.parse(cursor.getString(imageColumnIndex)));

        saleIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY));
                if (quantity > 0){
                    int newQuantity = quantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_ITEM_QUANTITY, newQuantity);
                    context.getContentResolver().update(uri,values,null,null);
                } else {
                    Toast.makeText(context,context.getString(R.string.product_out_of_stock), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
