package com.androidapp.crudexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnAdd;
    private DatabaseAccess databaseAccess;
    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.databaseAccess = DatabaseAccess.getInstance(this);
        this.listView = (ListView) findViewById(R.id.listView);
        this.btnAdd = (Button) findViewById(R.id.btnAdd);
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClicked();
            }
        });

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = notes.get(position);
                TextView txtNote = (TextView) view.findViewById(R.id.txtNote);
                if (note.isFullDisplayed()) {
                    txtNote.setText(note.getText());
                    note.setFullDisplayed(false);
                } else {
                    txtNote.setText(note.getText());
                    note.setFullDisplayed(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseAccess.open();
        this.notes = databaseAccess.getAllNotes();
        databaseAccess.close();
        NoteAdapter adapter = new NoteAdapter(this, notes);
        this.listView.setAdapter(adapter);
    }

    public void onAddClicked() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    public void onDeleteClicked(Note note) {
        databaseAccess.open();
        databaseAccess.delete(note);
        databaseAccess.close();

        ArrayAdapter<Note> adapter = (ArrayAdapter<Note>) listView.getAdapter();
        adapter.remove(note);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(Note note) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("NOTE", note);
        startActivity(intent);
    }

    private class NoteAdapter extends ArrayAdapter<Note> {
        public NoteAdapter(Context context, List<Note> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_list, parent, false);
            }

            ImageView btnEdit = (ImageView) convertView.findViewById(R.id.btnEdit);
            ImageView btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            TextView txtNote = (TextView) convertView.findViewById(R.id.txtNote);

            final Note note = notes.get(position);
            note.setFullDisplayed(false);
            txtDate.setText(note.getDate());
            txtNote.setText(note.getText());

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditClicked(note);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClicked(note);
                }
            });
            return convertView;
        }

    }

}