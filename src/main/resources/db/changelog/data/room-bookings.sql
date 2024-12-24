DO
$$
            DECLARE
booking_date DATE;
                start_time
TIME;
                end_time
TIME;
                room_id
INT;
                user_id
INT;
BEGIN
FOR i IN 1..50 LOOP
                    -- Generate random booking date between 2025-01-01 and 2025-01-03
                    booking_date := '2025-01-01'::DATE + (random() * 2)::INT;

                    -- Generate random start and end times (start_time < end_time)
                    start_time
:= TIME '08:00:00' + (random() * 10)::INT * INTERVAL '1 hour';
        end_time
:= start_time + (1 + random() * 2)::INT * INTERVAL '1 hour';

                    -- Randomly assign user_id and room_id between 1 and 5
        user_id
:= (random() * 4)::INT + 1;
        room_id
:= (random() * 4)::INT + 1;

                    -- Insert into room_booking table
INSERT INTO room_booking (booking_date, start_time, end_time, room_id, user_id, created_at, updated_at)
VALUES (booking_date,
        start_time,
        end_time,
        room_id,
        user_id,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);
END LOOP;
END $$;