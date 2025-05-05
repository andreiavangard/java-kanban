package adapters;

import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.time.Duration;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration duration) throws IOException {
        if (duration == null) {
            out.value(0);
        } else {
            out.value(duration.toMinutes());
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        String durationString = in.nextString();
        if (durationString == null || durationString.isEmpty()) {
            return null;
        }
        long durationInMinutes = Long.parseLong(durationString);
        return Duration.ofMinutes(durationInMinutes);
    }
}
