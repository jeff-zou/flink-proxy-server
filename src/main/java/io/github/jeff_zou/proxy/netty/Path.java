package io.github.jeff_zou.proxy.netty;

public class Path {
    private String method;
    private String uri;
    private boolean equal = true;

    public Path(String method, String uri, boolean equal) {
        this.method = method;
        this.uri = uri;
        this.equal = equal;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isEqual() {
        return equal;
    }

    public void setEqual(boolean equal) {
        this.equal = equal;
    }

    @Override
    public String toString() {
        return method.toUpperCase() + " " + uri.toUpperCase();
    }

    @Override
    public int hashCode() {
        return ("HTTP " + method.toUpperCase() + " " + uri.toUpperCase()).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Path) {
            Path path = (Path) object;
            return method.equalsIgnoreCase(path.method) && uri.equalsIgnoreCase(path.uri);
        }
        return false;
    }
}
