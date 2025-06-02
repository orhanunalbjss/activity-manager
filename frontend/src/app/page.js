'use client'

import {useState} from 'react';
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

function AddNewActivityButton({handleOpen}) {
  return (
    <Button variant="contained" onClick={handleOpen}>
      Add New Activity
    </Button>
  );
}

function AddRandomActivityButton() {
  return (
    <Button variant="contained">
      Add Random Activity
    </Button>
  );
}

function AddActivityButtons({handleOpen}) {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "row",
        gap: 1,
      }}
    >
      <AddNewActivityButton handleOpen={handleOpen}/>
      <AddRandomActivityButton/>
    </Box>
  );
}

function EditActivityButton({activity, handleOpenEdit}) {
  return (
    <Tooltip title="Edit">
      <IconButton
        onClick={() => handleOpenEdit(activity.name, activity.type, activity.participants)}
        color="primary"
        aria-label="edit">
        <EditIcon/>
      </IconButton>
    </Tooltip>
  );
}

function DeleteActivityButton() {
  return (
    <Tooltip title="Delete">
      <IconButton color="primary" aria-label="delete">
        <DeleteIcon/>
      </IconButton>
    </Tooltip>
  );
}

function ActivityActions({activity, handleOpenEdit}) {
  return (
    <Box align="center">
      <EditActivityButton activity={activity} handleOpenEdit={handleOpenEdit}/>
      <DeleteActivityButton/>
    </Box>
  );
}

function ActivityRow({activity, handleOpenEdit}) {
  return (
    <TableRow
      key={activity.name}
      sx={{'&:last-child td, &:last-child th': {border: 0}}}
    >
      <TableCell component="th" scope="row">
        {activity.name}
      </TableCell>
      <TableCell>{activity.type}</TableCell>
      <TableCell align="right">{activity.participants}</TableCell>
      <TableCell align="right"><ActivityActions activity={activity} handleOpenEdit={handleOpenEdit}/></TableCell>
    </TableRow>
  );
}

function ActivityTable({activities, handleOpenEdit}) {
  const activityRows = [];

  activities.forEach((activity) => {
    activityRows.push(
      <ActivityRow
        activity={activity}
        key={activity.name}
        handleOpenEdit={handleOpenEdit}
      />
    );
  });

  return (
    <TableContainer component={Paper}>
      <Table sx={{minWidth: 650}} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Type</TableCell>
            <TableCell align="right">Participants</TableCell>
            <TableCell align="center">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {activityRows}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

function AddEditActivityModal({open, handleClose, handleSave, handleChange, formData}) {
  return (
    <Dialog open={open} onClose={handleClose}>
      <DialogTitle>Add/Edit Activity</DialogTitle>
      <DialogContent>
        <TextField
          margin="normal"
          label="Name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          fullWidth
        />
        <TextField
          margin="normal"
          label="Type"
          name="type"
          value={formData.type}
          onChange={handleChange}
          fullWidth
        />
        <TextField
          margin="normal"
          label="Participants"
          name="participants"
          type="number"
          value={formData.participants}
          onChange={handleChange}
          fullWidth
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

function ActivityManagerApp({activities}) {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    type: "",
    participants: "",
  });

  const handleOpen = () => setOpen(true);
  const handleOpenEdit = (name, type, participants) => {
    setFormData({name: name, type: type, participants: participants,});
    setOpen(true);
  }
  const handleClose = () => {
    setFormData({name: "", type: "", participants: "",});
    setOpen(false);
  }

  const handleChange = (e) => {
    const {name, value} = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSave = () => {
    console.log("Form Data:", formData);
    handleClose();
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
      <AddActivityButtons handleOpen={handleOpen}/>
      <ActivityTable activities={activities}
                     handleOpenEdit={handleOpenEdit}/>
      <AddEditActivityModal open={open}
                            handleClose={handleClose}
                            handleSave={handleSave}
                            handleChange={handleChange}
                            formData={formData}
      />
    </Box>
  );
}

const ACTIVITIES = [
  {
    name: "My awesome activity!",
    type: "recreational",
    participants: 1
  },
]

export default function Home() {
  return <ActivityManagerApp activities={ACTIVITIES}/>
}
