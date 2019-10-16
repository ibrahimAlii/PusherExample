package ib.project.pusher;

/**
 * Wrapper class that serves as a union of a result value and an exception. When the download
 * task has completed, either the result value or exception can be a non-null value.
 * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
 */
class PushResult {


    private String resultValue;
    private Exception exception;

    PushResult(String resultValue) {
        this.resultValue = resultValue;
    }

    PushResult(Exception exception) {
        this.exception = exception;
    }

    Exception getException() {
        return exception;
    }

    String getResultValue() {
        return resultValue;
    }
}
