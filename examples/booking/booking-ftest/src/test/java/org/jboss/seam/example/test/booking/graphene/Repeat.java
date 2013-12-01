
package org.jboss.seam.example.test.booking.graphene;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
public @interface Repeat {
    
    public int value();
}
