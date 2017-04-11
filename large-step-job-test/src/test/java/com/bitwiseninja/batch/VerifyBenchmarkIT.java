package com.bitwiseninja.batch;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.SerializationUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

/**
 * Creates a Plotly.js HTML report of the method call times before/after the patch
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VerifyBenchmarkIT {

    private static final int MINIMUM_PERFORMANCE_THRESHOLD = 10;

    private static final String REPORT_FOLDER = "../benchmark-report";
    private static final Resource REPORT_TEMPLATE_RESOURCE = new FileSystemResource(REPORT_FOLDER + "/chart-template.html.txt");
    private static final File OUTPUT_REPORT_PATH = new File(REPORT_FOLDER + "/benchmark-report.html");
    private static final String RUN_METRIC_PREFIX = "runMetrics-";
    private static final String TIMESTAMP_PLACEHOLDER = "[timestamp]";
    private static final String X_AXIS_PLACEHOLDER = "[traceX]";
    private static final String Y_AXIS_PLACEHOLDER_FORMAT = "[%s]";
    private static final String IMPROVEMENT_FACTOR_PLACEHOLDER = "[improvementFactor]";

    private static String reportTemplate;
    private static String xAxis;
    private static File[] runResults;
    private static long improvementFactor;


    @BeforeClass
    public static void setupClass() throws Exception {

        System.out.println("Creating benchmark report from template: " + REPORT_TEMPLATE_RESOURCE.getFile().getCanonicalPath());

        reportTemplate = readToString(REPORT_TEMPLATE_RESOURCE.getInputStream());
        xAxis = populateXAxis();
        runResults = new File(REPORT_FOLDER).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(RUN_METRIC_PREFIX);
            }
        });

        // lexicographic: patched, unpatched
        Arrays.sort(runResults);
    }

    /**
     * Called by component tests to save their method performance metrics
     *
     * @param name       used to identify the test run, assumed unique
     * @param runMetrics profiled method call times
     * @throws FileNotFoundException if file cannot be written
     */
    public static void saveResults(String name, List<Long> runMetrics) throws IOException {
        System.out.println("Adding metrics: " + runMetrics);
        byte[] serializedRun = SerializationUtils.serialize(runMetrics);
        FileOutputStream fos = new FileOutputStream(format(REPORT_FOLDER + "/" + RUN_METRIC_PREFIX + "%s", name));
        fos.write(serializedRun);
        fos.close();
    }

    private static List<Long> readResults(File runMetricFile) throws IOException {

        byte[] b = new byte[(int) runMetricFile.length()];
        FileInputStream fileInputStream = new FileInputStream(runMetricFile);
        fileInputStream.read(b);

        return (List<Long>) SerializationUtils.deserialize(b);

    }

    @Test
    public void checkMeetsMinimumPerformance() throws Exception {
        // sanity check
        Assert.assertEquals("Need both unpatched and unpatched run results!", 2, runResults.length);

        // sorted based on filename: patched, unpatched
        long patchedTimeMillis = sum(readResults(runResults[0]));
        long unpatchedTimeMillis = sum(readResults(runResults[1]));

        improvementFactor = unpatchedTimeMillis / patchedTimeMillis;

        assertTrue(
                format("Performance increase not adequate! Expected at least [%d] times faster, but was [%d]",
                    MINIMUM_PERFORMANCE_THRESHOLD, improvementFactor),
                improvementFactor >= MINIMUM_PERFORMANCE_THRESHOLD);
    }

    private long sum(Collection<Long> numbers) {
        long sum = 0;
        for (Long num : numbers) {
            if (num != null) {
                sum += num;
            }
        }
        return sum;
    }

    @Test
    public void generateReport() throws Exception {

        String report = reportTemplate;

        for (File runMetricFile : runResults) {

            String yAxisKey = format(Y_AXIS_PLACEHOLDER_FORMAT,
                    runMetricFile.getName().replace(RUN_METRIC_PREFIX, ""));
            List<Long> yAxisValues = readResults(runMetricFile);

            report = report.replace(X_AXIS_PLACEHOLDER, xAxis);
            report = report.replace(yAxisKey, yAxisValues.toString());
        }

        report = report.replace(TIMESTAMP_PLACEHOLDER, SimpleDateFormat.getDateTimeInstance().format(new Date()));
        report = report.replace(IMPROVEMENT_FACTOR_PLACEHOLDER, new Long(improvementFactor).toString());

        System.out.println("Writing output report: " + OUTPUT_REPORT_PATH.getCanonicalPath());
        writeToFile(report);

    }

    private void writeToFile(String report) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(OUTPUT_REPORT_PATH);
        out.print(report);
        out.close();
    }

    private static String readToString(InputStream inputStream) throws IOException {
        // verbose, because compiling to Java 6
        BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));
        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        return sb.toString();
    }

    private static String populateXAxis() {
        StringBuilder xAxisBuilder = new StringBuilder("[ ");
        for (int i = 1; i <= JobRepositoryTestParent.GRID_SIZE_OUTER_PARTITIONER; i++) {
            xAxisBuilder.append(i + ", ");
        }
        xAxisBuilder.append("]");
        return xAxisBuilder.toString();
    }

}
