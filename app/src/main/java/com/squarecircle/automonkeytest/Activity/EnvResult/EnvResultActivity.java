package com.squarecircle.automonkeytest.Activity.EnvResult;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.squarecircle.automonkeytest.Activity.Main.Fragment.Env.EnvFragment;
import com.squarecircle.automonkeytest.R;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class EnvResultActivity extends AppCompatActivity implements Destroyable {
    
    protected final String TAG = this.getClass().getSimpleName();
    
    @BindView(R.id.env_result_toolbar) Toolbar toolbar;
    
    //chartView
    @BindView(R.id.env_result_chart) LineChartView chartView;
    List<PointValue> cpuList = new ArrayList<>();
    List<PointValue> memList = new ArrayList<>();
    List<Line> lineList = new ArrayList<>();
    int position = 0;
    int memMax = 100;
    private Axis axisMem = new Axis();
    
    //data
    int threadCount, pictureCount, CPUInterval, memoryInterval;
    float totalCPU = 0, idleCPU = 0, totalCPU2, idleCPU2, processCPU = 0, processCPU2;
    int CPUnum, processMemory;
    float totalCpuRatio, processCpuRatio;
    int pid = android.os.Process.myPid();
    
    //加载图片
    ArrayList<Bitmap> bmpList = new ArrayList<>();
    
    //开启线程
    ArrayList<Thread> threads = new ArrayList<>();
    boolean destroyed = false;
    
    //timers
    CountDownTimer cpuTimer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env_result);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        initChartView();
        
        geneData();

        loadPic();
        openThread();
        
        initCpuTimer();
        
        cpuTimer.start();
        
    }
    
    private void loadPic() {
        threads.add(new Thread(new Runnable() {
            
            @Override
            public void run() {
                for (int i = 0; i < pictureCount; i++) {
                    bmpList.add(BitmapFactory.decodeResource(getResources(), R.drawable.test));
                }
            }
        }));
    }
    
    private void openThread() {
        for (int i = 0; i < threadCount - 1; i++) {
            threads.add(new Thread(getRunnable()));
        }
        for (Thread thread : threads) {
            thread.start();
        }
    }
    
    private Runnable getRunnable() {
        return new MyRunnable(this);
    }
    
    @Override
    public boolean getDestroyed() {
        return destroyed;
    }
    
    private class MyRunnable implements Runnable {
        
        private Destroyable destroyable;
        
        public MyRunnable(Destroyable destroyable) {
            this.destroyable = destroyable;
        }
        
        @Override
        public void run() {
            while (!destroyable.getDestroyed()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void initCpuTimer() {
        cpuTimer = new CountDownTimer(1000000000, CPUInterval * 1000) {
            
            @Override
            public void onTick(long millisUntilFinished) {
                getTotalCpuTime();
                getProcessCpuTime();
                getPidMemorySize(EnvResultActivity.this);
                Log.d(TAG, "onTick");
                changeStatus();
            }
            
            @Override
            public void onFinish() {
                Toast.makeText(EnvResultActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        };
    }
    
    private void geneData() {
        Intent intent = getIntent();
        threadCount = intent.getIntExtra(EnvFragment.THREAD_COUNT, 20);
        pictureCount = intent.getIntExtra(EnvFragment.PICTURE_COUNT, 10);
        CPUInterval = intent.getIntExtra(EnvFragment.CPU_INTERVAL, 3);
        
        Log.d(TAG, "onCreate: " + getNumCores());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cpuTimer.cancel();
        
        for (Bitmap bmp : bmpList) {
            bmp.recycle();
        }
        
    }
    
    private void initChartView() {
        
        Axis axisTime = new Axis();
        Axis axisCpu = new Axis();
        
        axisTime.setName("时间");
        axisCpu.setName("百分比");
        axisMem.setName("内存占用量");
        
        ArrayList<AxisValue> values = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            AxisValue axisValue = new AxisValue(i * 10);
            axisValue.setLabel("" + i);
            values.add(axisValue);
        }
        axisCpu.setValues(values);
        axisCpu.setFormatter(new SimpleAxisValueFormatter().setAppendedText("%".toCharArray()));
        axisCpu.setHasLines(true);
        
        ArrayList<AxisValue> memValues = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            AxisValue axisValue = new AxisValue(i * 10);
            axisValue.setLabel("" + i);
            memValues.add(axisValue);
        }
        axisMem.setValues(memValues);
        
        //cpu折线蓝色
        Line cpuLine = new Line(cpuList);
        cpuLine.setColor(Color.BLUE);
        cpuLine.setShape(ValueShape.CIRCLE);
        cpuLine.setCubic(false);
        //memory折线红色
        Line memLine = new Line(memList);
        memLine.setColor(Color.RED);
        memLine.setShape(ValueShape.CIRCLE);
        memLine.setCubic(false);
        
        lineList.add(cpuLine);
        lineList.add(memLine);
        
        LineChartData data = new LineChartData(lineList);
        data.setAxisXBottom(axisTime);
        data.setAxisYLeft(axisCpu);
        data.setAxisYRight(axisMem);
        chartView.setLineChartData(data);
        
    }
    
    void changeStatus() {
        
        PointValue cpuPoint = new PointValue(position, totalCpuRatio);
        cpuList.add(cpuPoint);
        
        float processMemoryM = (float) ((processMemory - 1.0) / 1024);
        if (processMemoryM > memMax) {
            for (PointValue p : memList) {
                p.set(p.getX(), p.getY() * memMax / processMemoryM);
            }
            memMax = (int) (processMemoryM + 0.5);
        }
        
        //重新设置axisMem的label
        List<AxisValue> values = axisMem.getValues();
        for (int i = 0; i < 11; i++) {
            AxisValue axisValue = values.get(i);
            axisValue.setLabel("" + memMax / 10 * i);
        }
        
        float normMem = (processMemoryM / memMax) * 100;
        
        PointValue memPoint = new PointValue(position, normMem);
        memList.add(memPoint);
        
        chartView.setLineChartData(chartView.getLineChartData());
        Viewport viewport = chartView.getCurrentViewport();
    
        if (viewport.right > 15) {
            viewport.right += 1;
            viewport.left = viewport.right - 15;
        }
        viewport.top = 100;
        viewport.bottom = 0;
        chartView.setCurrentViewport(viewport);
        chartView.setMaximumViewport(viewport);
        
        position++;
    }
    
    public void getTotalCpuTime() {
        String totalCpuPath = "/proc/stat";
        try {
            totalCPU2 = totalCPU;
            idleCPU2 = idleCPU;
            
            RandomAccessFile file = new RandomAccessFile(totalCpuPath, "r");
            String[] toks = file.readLine().split(" ");
            idleCPU = Long.parseLong(toks[5]);
            totalCPU = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[5]) + Long.parseLong(toks[7])
                    + Long.parseLong(toks[8]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //获取总体CPU占用率，其中CPUnum为CPU核心数
        totalCpuRatio =
                (float) (100 * ((double) ((totalCPU - idleCPU) - (totalCPU2 - idleCPU2)) / (double) (
                        totalCPU - totalCPU2)));
    }
    
    public void getProcessCpuTime() {
        String processCpuPath = "/proc/" + pid + "/stat";
        try {
            processCPU2 = processCPU;
            
            RandomAccessFile file = new RandomAccessFile(processCpuPath, "r");
            String line = "";
            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            while ((line = file.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String[] toks = sb.toString().split(" ");
            processCPU =
                    Long.parseLong(toks[13]) + Long.parseLong(toks[14]) + Long.parseLong(toks[15])
                            + Long.parseLong(toks[16]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //获取某进程的CPU占用率，其中CPUnum为CPU核心数
        processCpuRatio =
                (int) (100 * CPUnum * ((double) (processCPU - processCPU2) / (double) (totalCPU
                        - totalCPU2)));
    }
    
    private int getNumCores() {
        class CpuFilter implements FileFilter {
            
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("cpu[0-9]", pathname.getName());
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
    
    public void getPidMemorySize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int[] myMempid = new int[]{ pid };
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
        memoryInfo[0].getTotalSharedDirty();
        processMemory = memoryInfo[0].getTotalPss();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }
    
    private static class HeightValueFormatter extends SimpleAxisValueFormatter {
        
        private float scale;
        private float sub;
        private int decimalDigits;
        
        public HeightValueFormatter(float scale, float sub, int decimalDigits) {
            this.scale = scale;
            this.sub = sub;
            this.decimalDigits = decimalDigits;
        }
        
        @Override
        public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value,
                int autoDecimalDigits) {
            float scaledValue = (value + sub) / scale;
            return super.formatValueForAutoGeneratedAxis(formattedValue, scaledValue,
                    this.decimalDigits);
        }
    }
    
}
