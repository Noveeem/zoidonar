package com.example.zoidonarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;


public class History extends AppCompatActivity {

    TextView txtDonorName, txtViewHistory, txtBT, txtID, txtUnit, txtGallon, txtPDF;

    private String[] QuotesList;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtPDF  = findViewById(R.id.txtPDF);
        txtGallon = findViewById(R.id.txtGallon);
        txtUnit = findViewById(R.id.txtUnit);
        txtID = findViewById(R.id.txtID);
        txtDonorName = findViewById(R.id.txtDonorName);
        txtViewHistory = findViewById(R.id.txtViewHistory);
        txtBT = findViewById(R.id.txtBT);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();



        reference.child("users").child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (currentUser != null)
                        {
                            txtDonorName.setText(user.firstName + " " + user.middleName + " " + user.lastName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        txtViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                archivedView();
            }
        });

        reference.child("users_donated").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int units = (int) snapshot.getChildrenCount();
                    txtUnit.setText(units+"");
                    for (DataSnapshot i : snapshot.getChildren())
                    {
                        int volume = i.child("volume").getValue(int.class);
                        double gallon = (units * volume) * 0.000264;
                        txtGallon.setText(String.valueOf(gallon));
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("users_blood_type").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    txtBT.setText(ds.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtID.setText(currentUser.getUid());


        //PDF Print
        txtPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPDF();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

    }


    public void createPDF() throws IOException {
        QuotesList = new String[]{
                "Be the reason for someone’s heartbeat.",
                "A single drop of blood can make a huge difference.",
                "Stay fit and eat right and donate blood.",
                "Be a saviour just by donating your blood.",
                "A blood donor is equal to a lifesaver",
                "One hour and you can save three lives with your blood.",
                "Donate blood and save lives."
        };

        Random random = new Random();
        int randomQuote = random.nextInt(QuotesList.length);


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        String name = txtDonorName.getText().toString();
        String bloodtype = txtBT.getText().toString();
        String times = txtUnit.getText().toString();
        String blood = txtGallon.getText().toString();

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, name+".pdf");
        OutputStream outputStream = new FileOutputStream(file);

        DeviceRgb colorRed = new DeviceRgb(192, 57, 43);
        DeviceRgb colorWhite = new DeviceRgb(255, 255, 255);

        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        PdfFont fontBold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
        PageSize pageSize = new PageSize(PageSize.A5);
        Document document = new Document(pdfDocument, pageSize);

        /*Background Image*/
        Drawable d0 = getDrawable(R.drawable.new3bgpdf);
        Bitmap bitmap0 = ((BitmapDrawable)d0).getBitmap();
        ByteArrayOutputStream stream0 = new ByteArrayOutputStream();
        bitmap0.compress(Bitmap.CompressFormat.PNG, 100, stream0);
        byte[] bitmapData0 = stream0.toByteArray();

        ImageData imgDate0 = ImageDataFactory.create(bitmapData0);
        Image img0 = new Image(imgDate0);



        /*Start of Table 1*/
        float columnWidth[] = {200, 200, 200};
        Table table1 = new Table(columnWidth).setHorizontalAlignment(HorizontalAlignment.CENTER);

        Drawable d1 = getDrawable(R.drawable.logo);
        Bitmap bitmap1 = ((BitmapDrawable)d1).getBitmap();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1);
        byte[] bitmapData1 = stream1.toByteArray();

        ImageData imgDate1 = ImageDataFactory.create(bitmapData1);
        Image img1 = new Image(imgDate1);
        img1.setHeight(60);

        table1.addCell(new Cell().add(img1).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        /*End of Table 1*/

        /*Start of Table 1 Semi*/

        Table semitable1 = new Table(columnWidth).setHorizontalAlignment(HorizontalAlignment.CENTER);
        semitable1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        semitable1.addCell(new Cell().add(new Paragraph("DONOR CARD")).setBorder(Border.NO_BORDER).setFontSize(19f).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setBold().setBold());
        semitable1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        /*End of Table 1 Semi*/


        /*Start of Table 2*/
        float columnWidth2[] = {200};
        Table table2 = new Table(columnWidth2).setHorizontalAlignment(HorizontalAlignment.CENTER);

        table2.addCell(new Cell(1, 1).add(new Paragraph("\nDONOR ID")).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFontColor(colorRed).setBold().setBold().setBorder(Border.NO_BORDER));
        table2.addCell(new Cell(1, 1).add(new Paragraph(currentUser.getUid())).setTextAlignment(TextAlignment.CENTER).setBold().setFont(fontBold).setBorder(Border.NO_BORDER));

        table2.addCell(new Cell(1, 1).add(new Paragraph("\nDONOR NAME")).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFontColor(colorRed).setBold().setBorder(Border.NO_BORDER));
        table2.addCell(new Cell(1,1).add(new Paragraph(name)).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setBold().setBorder(Border.NO_BORDER));

        table2.addCell(new Cell(1, 1).add(new Paragraph("\nBLOOD TYPE")).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFontColor(colorRed).setBold().setBorder(Border.NO_BORDER));
        table2.addCell(new Cell(1,1).add(new Paragraph(bloodtype)).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setBold().setBorder(Border.NO_BORDER));

        /*End of Table 2*/

        /*Start of Table 2*/
        float columnWidth3[] = {240, 240};
        Table table3 = new Table(columnWidth3).setHorizontalAlignment(HorizontalAlignment.CENTER);

        table3.addCell(new Cell(2, 1).add(new Paragraph("TIMES DONATED")).setFontSize(12f).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFontColor(colorRed).setBold().setBold().setBorder(Border.NO_BORDER));
        table3.addCell(new Cell(2, 1).add(new Paragraph("BLOOD DONATED")).setFontSize(12f).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setFontColor(colorRed).setBold().setBold().setBorder(Border.NO_BORDER));

        table3.addCell(new Cell(2,1).add(new Paragraph(times)).setFontSize(16f).setFont(fontBold).setTextAlignment(TextAlignment.CENTER).setBold().setBorder(Border.NO_BORDER));
        table3.addCell(new Cell(2,1).add(new Paragraph(blood)).setFontSize(16f).setFont(fontBold).setTextAlignment(TextAlignment.CENTER).setBold().setBorder(Border.NO_BORDER));

        table3.addCell(new Cell(2, 1).add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table3.addCell(new Cell(2,1).add(new Paragraph("GALLON")).setFontSize(11f).setTextAlignment(TextAlignment.CENTER ).setFont(fontBold).setBold().setBorder(Border.NO_BORDER));

        /*End of Table 2*/

        /*Start of Table 2 Semi*/
        float semi2ColumnWidth[] = {300};
        Table semitable2 = new Table(semi2ColumnWidth).setHorizontalAlignment(HorizontalAlignment.CENTER);
        semitable2.addCell(new Cell().add(new Paragraph("'"+QuotesList[randomQuote]+"'")).setFontColor(colorWhite).setBorder(Border.NO_BORDER).setItalic().setFontSize(8f).setTextAlignment(TextAlignment.CENTER).setFont(fontBold).setBold().setBold());
        /*End of Table 2 Semi*/

        document.add(img0.setFixedPosition(0,0));
        document.add(table1);
        document.add(semitable1);
        document.add(new Paragraph("\n"));
        document.add(table2);
        document.add(new Paragraph("\n\n"));
        document.add(table3);
        document.add(new Paragraph("\n\n\n\n"));
        document.add(semitable2);
        document.close();
        Toast.makeText(this, "Pdf Document has successfully downloaded!", Toast.LENGTH_SHORT).show();

    }

    public void archivedView(){
        Intent i = new Intent(this, Archived.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_2, R.anim.slide_out_2);
        return true;
    }
}