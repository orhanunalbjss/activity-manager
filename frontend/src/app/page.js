'use client'

import {useEffect, useState} from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import TextField from '@mui/material/TextField';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";
import {CircularProgress} from "@mui/material";

function AddNewActivityButton({loading, handleOpenAddDialog}) {
  return (
    <Button variant="contained" disabled={loading} onClick={handleOpenAddDialog}>
      Add New Activity
    </Button>
  );
}

function AddRandomActivityButton({loading, handleAddRandomActivity}) {
  return (
    <Button variant="contained" disabled={loading} onClick={handleAddRandomActivity}>
      Add Random Activity
    </Button>
  );
}

function AddActivityButtons({loading, handleOpenAddDialog, handleAddRandomActivity}) {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "row",
        gap: 1,
      }}
    >
      <AddNewActivityButton loading={loading} handleOpenAddDialog={handleOpenAddDialog}/>
      <AddRandomActivityButton loading={loading} handleAddRandomActivity={handleAddRandomActivity}/>
    </Box>
  );
}

function EditActivityButton({activity, onEdit}) {
  return (
    <Tooltip title="Edit">
      <IconButton
        onClick={() => onEdit(activity)}
        color="primary"
        aria-label="edit">
        <EditIcon/>
      </IconButton>
    </Tooltip>
  );
}

function DeleteActivityButton({id, onDelete}) {
  return (
    <Tooltip title="Delete">
      <IconButton
        onClick={() => onDelete(id)}
        color="primary"
        aria-label="delete">
        <DeleteIcon/>
      </IconButton>
    </Tooltip>
  );
}

function ActivityActions({activity, onEdit, onDelete}) {
  return (
    <Box align="center">
      <EditActivityButton activity={activity} onEdit={onEdit}/>
      <DeleteActivityButton id={activity.id} onDelete={onDelete}/>
    </Box>
  );
}

function ActivityRow({activity, onEdit, onDelete}) {
  return (
    <TableRow
      sx={{'&:last-child td, &:last-child th': {border: 0}}}
    >
      <TableCell>{activity.name}</TableCell>
      <TableCell>{activity.type}</TableCell>
      <TableCell align="right">{activity.participants}</TableCell>
      <TableCell align="right">
        <ActivityActions activity={activity} onEdit={onEdit} onDelete={onDelete}/>
      </TableCell>
    </TableRow>
  );
}

function ActivityTable({activities, onEdit, onDelete}) {
  return (
    <TableContainer component={Paper}>
      <Table sx={{minWidth: 650}}>
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Type</TableCell>
            <TableCell align="right">Participants</TableCell>
            <TableCell align="center">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {activities.map((activity) =>
            <ActivityRow
              key={activity.id}
              activity={activity}
              onEdit={onEdit}
              onDelete={onDelete}/>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

function ActivityDialog({open, onClose, onSave, editingActivity}) {
  const [activity, setActivity] = useState({name: "", type: "", participants: ""});

  useEffect(() => {
    if (editingActivity) {
      setActivity(editingActivity);
    } else {
      setActivity({name: "", type: "", participants: ""});
    }
  }, [editingActivity]);

  const handleSave = () => {
    onSave(activity);
    handleClose();
  };

  const handleClose = () => {
    setActivity({name: "", type: "", participants: ""});
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>{editingActivity ? "Edit Activity" : "Create Activity"}</DialogTitle>
      <DialogContent>
        <TextField
          fullWidth
          margin="normal"
          label="Name"
          value={activity.name}
          onChange={(e) => setActivity(
            {...activity, name: e.target.value}
          )}
        />
        <TextField
          fullWidth
          margin="normal"
          label="Type"
          value={activity.type}
          onChange={(e) => setActivity(
            {...activity, type: e.target.value}
          )}
        />
        <TextField
          fullWidth
          margin="normal"
          label="Participants"
          type="number"
          value={activity.participants}
          onChange={(e) => setActivity(
            {...activity, participants: e.target.value}
          )}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={handleSave} color="primary">
          Save
        </Button>
        <Button onClick={handleClose} color="secondary">
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  );
}

function ActivityManagerApp() {
  const [activities, setActivities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [openDialog, setOpenDialog] = useState(false);
  const [editingActivity, setEditingActivity] = useState(null);

  const fetchData = () => {
    setLoading(true);
    fetch("http://localhost:8080/activities")
      .then((res) => res.json())
      .then((data) => {
        setActivities(data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchData()
  }, []);

  const handleAddRandomActivity = () => {
    fetch("http://localhost:8080/activities/random", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
    })
      .then((res) => res.json())
      .then(() => {
        fetchData();
      });
  };

  const handleOpenAddDialog = () => {
    setEditingActivity(null);
    setOpenDialog(true);
  }

  const handleOpenEditDialog = (activity) => {
    setEditingActivity({
      id: activity.id,
      name: activity.name ?? "",
      type: activity.type ?? "",
      participants: activity.participants ?? "",
    });
    setOpenDialog(true);
  }

  const handleSaveActivity = (activity) => {
    const method = editingActivity ? "PUT" : "POST";
    const url = editingActivity
      ? `http://localhost:8080/activities/${editingActivity.id}`
      : "http://localhost:8080/activities";
    const jsonWithoutId = JSON.stringify(activity, (key, value) => {
      return key === "id" ? undefined : value;
    });

    fetch(url, {
      method,
      body: jsonWithoutId,
      headers: {"Content-Type": "application/json"},
    })
      .then((res) => res.json())
      .then(() => {
        fetchData();
        setOpenDialog(false);
      });
  };

  const handleDeleteActivity = (id) => {
    fetch(`http://localhost:8080/activities/${id}`, {
      method: "DELETE",
    }).then(() => fetchData());
  };

  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        gap: 2,
        padding: 2,
      }}
    >
      <Typography variant="h4" component="h1" gutterBottom>
        Activity Manager
      </Typography>
      <Typography variant="body1" gutterBottom>
        Activity Manager is an application that helps you to organise your activities.
        You can add, update, or delete activities.
        You can also add a random activity, if you need a little inspiration.
      </Typography>
      <AddActivityButtons
        loading={loading}
        handleOpenAddDialog={handleOpenAddDialog}
        handleAddRandomActivity={handleAddRandomActivity}/>
      {loading ? (
        <Box sx={{display: "flex", justifyContent: "center", mt: 3}}>
          <CircularProgress/>
        </Box>
      ) : (
        <ActivityTable
          activities={activities}
          onEdit={handleOpenEditDialog}
          onDelete={handleDeleteActivity}
        />
      )}
      <ActivityDialog
        open={openDialog}
        onClose={() => setOpenDialog(false)}
        onSave={handleSaveActivity}
        editingActivity={editingActivity}
      />
    </Box>
  );
}

export default function Home() {
  return <ActivityManagerApp/>
}
