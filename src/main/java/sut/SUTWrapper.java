package sut;

import java.io.IOException;

public interface SUTWrapper {
    void pre();

    void post() throws IOException;

    Class getDeclaringClass();

}
