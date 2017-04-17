package uk.co.cdevelop.fabvocab.Support;

import android.util.Log;

import java.util.Stack;

/**
 * Created by Chris on 24/02/2017.
 */

public class Helpers {

    public static int minEditDistance(String a, String b) {

        int m = a.length();
        int n = b.length();

        // Create a table to store results of subproblems
        int[][] dp = new int[m][n];

        // Fill d[][] in bottom up manner
        for(int i = 0; i < m; i++) {
            for(int j = 0; j < n; j++) {

                //If first string is empty, only option is to
                //isnert all characters of second string
                if(i == 0) {
                    dp[i][j] = j; // Min.operations = j
                }

                // If second string is empty, only option is to
                // remove all characters of second string
                else if(j==0) {
                    dp[i][j] = i; // Min.operations = i
                }

                // If last characters are same, ignore last char
                // and recur for remaining string
                else if( a.charAt(i - 1) == b.charAt(j - 1)){
                    dp[i][j] = dp[i - 1][j - 1];
                }

                // If last character are different, consider all
                // possibilities and find minimum
                else {
                    dp[i][j] = 1 + Math.min(dp[i][j - 1], // Insert
                                   Math.min(dp[i - 1][j], // Remove
                                   dp[i - 1][j - 1]));    //Replace
                }

            }
        }

        return dp[m - 1][n - 1];

    }

    public static boolean isSimilar(String a, String b) {
        return isSimilar(a, b, 0.2);
    }

    public static boolean isSimilar(String a, String b, double percent) {

        if(a == b) {
            System.out.println("Same string");
        } else if (a.length() == 0 || b.length() == 0) {
            return false;
        } else if (a.length() == 0 && b.length() == 0) {
            return true;
        }


        int editDistance = minEditDistance(a, b);

        double prct = editDistance / (double) Math.max(a.length(), b.length());

        //System.out.println("Edit Distance: " + Integer.toString(editDistance));
        //System.out.println(" Prcnt of MAX: " + Float.toString(prctLongest * 100));
        //System.out.println(" Prcnt of MIN: " + Float.toString(prctShortest * 100));

        Log.i("ed123", Double.toString(prct));
        return (prct < percent);
    }

    public static int largestRectangleArea(int[] height) {
        if (height == null || height.length == 0) {
            return 0;
        }

        Stack<Integer> stack = new Stack<>();

        int max = 0;
        int i = 0;

        while (i < height.length) {
            //push index to stack when the current height is larger than the previous one
            if (stack.isEmpty() || height[i] >= height[stack.peek()]) {
                stack.push(i);
                i++;
            } else {
                //calculate max value when the current height is less than the previous one
                int p = stack.pop();
                int h = height[p];
                int w = stack.isEmpty() ? i : i - stack.peek() - 1;
                max = Math.max(h * w, max);
            }

        }

        while (!stack.isEmpty()) {
            int p = stack.pop();
            int h = height[p];
            int w = stack.isEmpty() ? i : i - stack.peek() - 1;
            max = Math.max(h * w, max);
        }

        return max;
    }

    public static void main(String args[]) {
        int answer = largestRectangleArea(new int[]{2, 3, 3, 1, 2});
    }
}
