CREATE TABLE workout_templates (
    id UUID PRIMARY KEY,
    owner_id UUID,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT workout_templates_name_not_blank CHECK (btrim(name) <> '')
);

CREATE TABLE template_exercises (
    id UUID PRIMARY KEY,
    workout_template_id UUID NOT NULL REFERENCES workout_templates(id) ON DELETE CASCADE,
    position_in_template INTEGER NOT NULL,
    exercise_id UUID NOT NULL,
    CONSTRAINT template_exercises_position_non_negative CHECK (position_in_template >= 0),
    CONSTRAINT template_exercises_position_unique UNIQUE (workout_template_id, position_in_template)
);

CREATE TABLE template_sets (
    id UUID PRIMARY KEY,
    template_exercise_id UUID NOT NULL REFERENCES template_exercises(id) ON DELETE CASCADE,
    position_in_exercise INTEGER NOT NULL,
    target_reps INTEGER NOT NULL,
    target_weight NUMERIC(12, 3) NOT NULL,
    target_weight_unit VARCHAR(8) NOT NULL,
    CONSTRAINT template_sets_position_non_negative CHECK (position_in_exercise >= 0),
    CONSTRAINT template_sets_reps_non_negative CHECK (target_reps >= 0),
    CONSTRAINT template_sets_weight_non_negative CHECK (target_weight >= 0),
    CONSTRAINT template_sets_weight_unit_valid CHECK (target_weight_unit IN ('KG', 'LB')),
    CONSTRAINT template_sets_position_unique UNIQUE (template_exercise_id, position_in_exercise)
);

CREATE INDEX template_exercises_template_id_idx
    ON template_exercises(workout_template_id);

CREATE INDEX template_sets_exercise_id_idx
    ON template_sets(template_exercise_id);

CREATE INDEX workout_templates_owner_id_idx
    ON workout_templates(owner_id);
