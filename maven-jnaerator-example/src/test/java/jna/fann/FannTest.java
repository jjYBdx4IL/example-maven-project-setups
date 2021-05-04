package jna.fann;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.bridj.Pointer;
import org.bridj.Pointer.StringType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FannTest {

    private static void testRun(Pointer<fann> ann, float in1, float in2, float expectedResult) {
        Pointer<Float> input = Pointer.allocateFloats(2);
        input.set(0, in1);
        input.set(1, in2);
        Pointer<Float> result = FannLibrary.fann_run(ann, input);
        assertEquals(expectedResult, result.get(0), .1f);
    }
    
    @Test
    public void testXORTrain() throws IOException, URISyntaxException {
        File xorDataFile = new File(getClass().getResource("xor.data").toURI());

        final int num_input = 2;
        final int num_output = 1;
        final int num_layers = 3;
        final int num_neurons_hidden = 3;
        final float desired_error = .001f;
        final int max_epochs = 500000;
        final int epochs_between_reports = 1000;
        final FannLibrary.fann_activationfunc_enum activation = FannLibrary.fann_activationfunc_enum.FANN_SIGMOID_SYMMETRIC;
        Pointer<fann> ann;
        Pointer<fann_train_data> data;
        Pointer<Byte> filename = (Pointer<Byte>) Pointer.pointerToString(xorDataFile.getAbsolutePath(), StringType.C, Charset.forName("UTF-8"));

        ann = FannLibrary.fann_create_standard(num_layers, num_input, num_neurons_hidden, num_output);
        FannLibrary.fann_set_training_algorithm(ann, FannLibrary.fann_train_enum.FANN_TRAIN_QUICKPROP);

        FannLibrary.fann_set_activation_function(ann, activation, 1, 0);
        FannLibrary.fann_set_activation_steepness(ann, .5f, 1, 0);
        FannLibrary.fann_set_activation_function(ann, activation, 1, 1);
        FannLibrary.fann_set_activation_steepness(ann, .5f, 1, 1);
        FannLibrary.fann_set_activation_function(ann, activation, 1, 2);
        FannLibrary.fann_set_activation_steepness(ann, .5f, 1, 2);
        FannLibrary.fann_set_activation_function(ann, activation, 2, 0);
        FannLibrary.fann_set_activation_steepness(ann, .5f, 2, 0);

        FannLibrary.fann_train_on_file(ann, filename, max_epochs, epochs_between_reports, desired_error);
        float mse = FannLibrary.fann_get_MSE(ann);
        assertTrue(mse <= desired_error);

        testRun(ann, -1, -1, -1);
        testRun(ann,  1,  1, -1);
        testRun(ann, -1,  1,  1);
        testRun(ann,  1, -1,  1);

        FannLibrary.fann_destroy(ann);
    }
}
