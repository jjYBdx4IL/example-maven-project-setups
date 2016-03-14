package jna.fann;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.bridj.Pointer;
import org.bridj.Pointer.StringType;
import static org.bridj.Pointer.pointerToString;
import org.junit.Test;

import static jna.fann.FannLibrary.fann_create_standard;

public class FannTest {


    /**
     * This test is re-implementation of the xor train/test example included with the FANN library sources.
     * It's not complete and currently only checks whether the fann library gets loaded.
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testTrainAndRun() throws IOException, URISyntaxException {
        File xorDataFile = new File(getClass().getResource("xor.data").toURI());

        final int num_input = 2;
        final int num_output = 1;
        final int num_layers = 3;
        final int num_neurons_hidden = 3;
        final float desired_error = 0f;
        final int max_epochs = 1000;
        final int epochs_between_reports = 10;
        Pointer<fann> ann;
        Pointer<fann_train_data> data;

        ann = fann_create_standard(num_layers, num_input, num_neurons_hidden, num_output);
        data = FannLibrary.fann_read_train_from_file(
                (Pointer<Byte>)pointerToString(xorDataFile.getAbsolutePath(), StringType.C, Charset.forName("UTF-8")));
        
    }
}
