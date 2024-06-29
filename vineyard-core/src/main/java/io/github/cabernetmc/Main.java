package io.github.cabernetmc;

import io.github.cabernetmc.execution.VineyardExecutionFactory;
import io.github.cabernetmc.execution.VineyardExecutionSettings;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        final var vineyard = new VineyardExecutionFactory(new VineyardExecutionSettings(
                true,
                (s) -> System.out.println(s),
                false,
                Path.of("vineyard-work"),
                "1.21"
        ));

        final var meta = vineyard.createMetaExecution().run().unwrap();
        vineyard.createDownloadExecution(meta).run();
        vineyard.createExtractExecution().run();
        vineyard.createRemapExecution().run();
        vineyard.createDecompileExecution(meta).run();
    }

}
