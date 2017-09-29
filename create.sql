--Create script for todolist-database

--Table projects
CREATE TABLE public.projects
(
  id serial NOT NULL,
  name text NOT NULL,
  "position" integer NOT NULL,
  CONSTRAINT projects_pkey PRIMARY KEY (id),
  CONSTRAINT projects_name_key UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.projects
  OWNER TO postgres;
  
--Table tasks
CREATE TABLE public.tasks
(
  id serial NOT NULL,
  project_id integer,
  task text NOT NULL,
  parent_task_id integer,
  "position" integer NOT NULL,
  CONSTRAINT tasks_pkey PRIMARY KEY (id),
  CONSTRAINT tasks_parent_task_id_fkey FOREIGN KEY (parent_task_id)
      REFERENCES public.tasks (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT tasks_project_id_fkey FOREIGN KEY (project_id)
      REFERENCES public.projects (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.tasks
  OWNER TO postgres;

-- Index: public.fki_tasks_parent_task_id_fkey

-- DROP INDEX public.fki_tasks_parent_task_id_fkey;

CREATE INDEX fki_tasks_parent_task_id_fkey
  ON public.tasks
  USING btree
  (parent_task_id);