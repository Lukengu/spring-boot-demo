package pro.novatech.solutions.api.banking.enums;

public enum AccountType {
    CREDIT("CREDIT"),
    DEBIT("DEBIT");

    public final String label;
    private AccountType(String label) {
        this.label = label;
    }
}
