package pl.foltak.mybudget.rest.e2e.helper;

/**
 *
 * @author mfoltak
 */
public enum HttpStatusCode {

    OK("OK", 200), CREATED("Created", 201), BAD_REQUEST("Bad Request", 400), UNAUTHORIZED(
            "Unauthorized",
            401), NOT_FOUND("Not Found", 404), CONFLICT("Conflict", 409);
    private final String name;
    private final Integer code;

    private HttpStatusCode(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public static HttpStatusCode fromName(String name) {
        for (HttpStatusCode httpStatusCode : values()) {
            if (httpStatusCode.name.equals(name)) {
                return httpStatusCode;
            }
        }
        throw new IllegalArgumentException("Enum with name " + name + " doesn't exist");
    }

}
