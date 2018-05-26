package io.github.jlameira.encoding;

public interface IZencoderService {

    void initial();

    String encode(
            String input_filename,
            String input_path,
            String output_filename,
            String output_path
    );

}
