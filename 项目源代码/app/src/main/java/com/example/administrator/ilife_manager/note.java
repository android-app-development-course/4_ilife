package com.example.administrator.ilife_manager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class note extends AppCompatActivity {

    NoteHelper helper;
    RecyclerView recyclerView;
    FloatingActionButton addNote;
    List<String> datas;
    HomeAdapter myHomeAdapter=new HomeAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);
        helper=new NoteHelper(this);

        recyclerView=findViewById(R.id.noteRecyclerview);
        initData();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        recyclerView.setAdapter(myHomeAdapter);

        addNote=(FloatingActionButton)findViewById(R.id.floatingBtn);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(note.this,editNote.class);
                startActivityForResult(intent,1);
            }
        });

    }

    protected void initData()
    {
        datas = new ArrayList<String>();
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.query("notebook",null,null,null,null,null,null);
        if(cursor.getCount()!=0) {
            cursor.moveToFirst();
            String noteContent=cursor.getString(3);
            if(noteContent.length()>50) noteContent=noteContent.substring(0,50)+"...";
            datas.add(noteContent);
            while (cursor.moveToNext()) {
                noteContent=cursor.getString(3);
                if(noteContent.length()>50) noteContent=noteContent.substring(0,50);
                datas.add(noteContent);
            }
        }
        cursor.close();
        db.close();
    }
    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
    {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    note.this).inflate(R.layout.note_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            holder.tv.setText(datas.get(position));

        }

        @Override
        public int getItemCount()
        {
            return datas.size();
        }

        public void addData(int position) {

            SQLiteDatabase db=helper.getReadableDatabase();
            Cursor cursor=db.query("notebook",null,null,null,null,null,null);
            cursor.moveToLast();
            String noteContent=cursor.getString(3);
            if(noteContent.length()>50) noteContent=noteContent.substring(0,50)+"...";
            datas.add(position, noteContent);
            notifyItemInserted(position);
        }

        public void removeData(int position) {
            datas.remove(position);
            notifyItemRemoved(position);
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            TextView tv;

            public MyViewHolder(View view)
            {
                super(view);
                tv = (TextView) view.findViewById(R.id.noteContentTV);
            }
        }


    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1)
        {
          myHomeAdapter.addData(0);
        }
    }
}
