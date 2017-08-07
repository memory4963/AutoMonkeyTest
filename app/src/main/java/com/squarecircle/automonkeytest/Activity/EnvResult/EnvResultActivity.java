package com.squarecircle.automonkeytest.Activity.EnvResult;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class EnvResultActivity extends AppCompatActivity {
    
    protected final String TAG = this.getClass().getSimpleName();
    
    @BindView(R.id.env_result_toolbar) Toolbar toolbar;
    @BindView(R.id.env_result_chart) LineChartView chartView;
    List<PointValue> cpuList = new ArrayList<>();
    List<PointValue> memList = new ArrayList<>();
    List<Line> lineList = new ArrayList<>();
    int position = 0;
    
    Axis axisx = new Axis();
    Axis axisy = new Axis();
    
    int threadCount, pictureCount, CPUInterval, memoryInterval;
    
    float totalCPU = 0, idleCPU = 0, totalCPU2, idleCPU2, processCPU = 0, processCPU2;
    int CPUnum, processMemory;
    float totalCpuRatio, processCpuRatio;
    int pid = android.os.Process.myPid();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env_result);
        ButterKnife.bind(this);
        
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        
        Intent intent = getIntent();
        threadCount = intent.getIntExtra(EnvFragment.THREAD_COUNT, 20);
        pictureCount = intent.getIntExtra(EnvFragment.PICTURE_COUNT, 100);
        CPUInterval = intent.getIntExtra(EnvFragment.CPU_INTERVAL, 3);
        memoryInterval = intent.getIntExtra(EnvFragment.MEMORY_INTERVAL, 3);
        
        axisx.setName("时间");
        axisy.setName("百分比");
        
        Log.d(TAG, "onCreate: " + getNumCores());
        
        CountDownTimer timer = new CountDownTimer(1000000000, 500) {
            
            @Override
            public void onTick(long millisUntilFinished) {
                getTotalCpuTime();
                getProcessCpuTime();
                getPidMemorySize(EnvResultActivity.this);
                changeData();
            }
            
            @Override
            public void onFinish() {
                Toast.makeText(EnvResultActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        };
        timer.start();
        
    }
    
    void changeData() {
        
        PointValue cpuPoint = new PointValue(position, totalCpuRatio);
        cpuList.add(cpuPoint);
        Line cpuLine = new Line(cpuList);
        cpuLine.setColor(Color.BLUE);
        cpuLine.setShape(ValueShape.CIRCLE);
        cpuLine.setCubic(false);
    
        PointValue memPoint = new PointValue(position, processMemory);
        memList.add(memPoint);
        Line memLine = new Line(memList);
        memLine.setColor(Color.RED);
        memLine.setShape(ValueShape.CIRCLE);
        memLine.setCubic(false);
        
        lineList.clear();
        lineList.add(cpuLine);
//        lineList.add(memLine);
        
        LineChartData data = new LineChartData(lineList);
        data.setAxisXBottom(axisx);
        data.setAxisYLeft(axisy);
        chartView.setLineChartData(data);
        
        float x = cpuPoint.getX();
        Viewport viewport;
        if (x > 10) {
            viewport = initViewPort(x - 10, x);
        } else {
            viewport = initViewPort(0, 10);
        }
        chartView.setCurrentViewport(viewport);
        
        Viewport maxViewPort = initMaxViewPort(x);
        chartView.setMaximumViewport(maxViewPort);
        
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
                (int) (100 * ((double) ((totalCPU - idleCPU) - (totalCPU2 - idleCPU2)) / (double) (
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
    
    private Viewport initViewPort(float left, float right) {
        Viewport port = new Viewport();
        port.top = 100;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }
    
    private Viewport initMaxViewPort(float right) {
        Viewport port = new Viewport();
        port.top = 100;
        port.bottom = 0;
        port.left = 0;
        port.right = right + 10;
        return port;
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
    
}
