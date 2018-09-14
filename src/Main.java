public class Main {

    static double x = 0;

    public static double sumSqrt(double[] a, int start, int end, int stride) {
        double sum = 0.0;
        for (int i = start; i < end; i += stride)
            sum += a[i];

        return sum;
    }


    public static void cacheItUp() {
        long[] arr = new long[300_000_000];
        for (int i = 0; i < arr.length; ++i)
            arr[i] = (long)(Math.random() * 100);

        long time1;


        long res = 0;

        time1 = System.nanoTime();
        for (int i=0; i<arr.length; i++) {
                res += arr[i] * arr[i];
        }

        System.out.println("normal took " + (System.nanoTime()-time1)/ 1_000_000_000.0);
        System.out.println(res);

        res = 0;

        int size = arr.length/8;

        time1 = System.nanoTime();
        for (int i=0; i<7; i++) {
            for (int j=i*size; j < (i+1)*size; j++)
                res += arr[j] * arr[j];
        }
        for (int j = 7*size; j < arr.length; j++)
            res += arr[j] * arr[j];

        System.out.println("chunks took " + (System.nanoTime()-time1)/ 1_000_000_000.0);
        System.out.println(res);

        res = 0;

        time1 = System.nanoTime();
        for (int i=0; i<8; i++) {
            for (int j=i; j < arr.length; j += 8)
                res += arr[j] * arr[j];
        }
        System.out.println("stripes took " + (System.nanoTime()-time1)/ 1_000_000_000.0);
        System.out.println(res);



    }

    public static void threadEx() {
        try {
            double[] arr = new double[300_000_000];

            for (int i = 0; i < arr.length; ++i)
                arr[i] = Math.random() * 100;

            long time1;

            System.out.println("Init done");

            time1 = System.nanoTime();
            double ret = sumSqrt(arr, 0, arr.length, 1);
            System.out.println("\t\tSync took " + (System.nanoTime() - time1) / 1_000_000_000.0);
            System.out.println(ret);

            // do sync test again; make sure JVM startup issues aren't impacting timing
            time1 = System.nanoTime();
            ret = sumSqrt(arr, 0, arr.length, 1);
            System.out.println("\t\tSync (again) took " + (System.nanoTime() - time1) / 1_000_000_000.0);
            System.out.println(ret);

            // the number of threads in the below two tests
            int N = 32;

            int size = arr.length / N;
            double[] res = new double[N];

            time1 = System.nanoTime();

            Thread[] threads = new Thread[N];
            for (int i = 0; i < N - 1; ++i) {
                final int pos = i; // why is this necessary? Let's chat.
                threads[i] = new Thread(() -> {
                    res[pos] = sumSqrt(arr, size * pos, size * (pos + 1), 1);
                });
                threads[i].start();
            }
            threads[N - 1] = new Thread(() -> {
                res[N - 1] = sumSqrt(arr, size * (N - 1), arr.length, 1);
            });
            threads[N - 1].start();

            for (Thread th : threads)
                th.join();

            double ret2 = 0.0;
            for (double d : res)
                ret2 += d;

            System.out.println("\t\tAsync took " + (System.nanoTime() - time1) / 1_000_000_000.0);
            System.out.println(ret2);

            time1 = System.nanoTime();

            Thread[] threads2 = new Thread[N];
            for (int i = 0; i < N; ++i) {
                final int pos = i;
                threads2[i] = new Thread(() -> {
                    res[pos] = sumSqrt(arr, pos, arr.length, N);
                });
                threads2[i].start();
            }

            for (Thread th : threads2)
                th.join();

            ret2 = 0.0;
            for (double d : res)
                ret2 += d;

            System.out.println("\t\tAsync2 took " + (System.nanoTime() - time1) / 1_000_000_000.0);
            System.out.println(ret2);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        cacheItUp();
        cacheItUp();
        cacheItUp();
    }

}
