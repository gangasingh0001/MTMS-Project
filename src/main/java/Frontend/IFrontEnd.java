package Frontend;

public interface IFrontEnd {
    public void rmIsDown(int rmNumber);
    public void sendRequestToSequencer(String request);
    public void rmHasBug(int rmNumber);
    public void resendRequest(int retryNumber);
}
