import * as React from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import '../AssetCard/AssetCard.css';
import { FaChartLine } from 'react-icons/fa';

export default function CreateInvestmentDialog({assetId}) {
  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <React.Fragment>
      <button className="invest-button" onClick={handleClickOpen}>
        <FaChartLine /> Investir
      </button>
      <Dialog
        open={open}
        onClose={handleClose}
        slotProps={{
          paper: {
            component: 'form',
            onSubmit: (event) => {
              event.preventDefault();
              const formData = new FormData(event.currentTarget);
              const formJson = Object.fromEntries(formData.entries());
              const initialValue = formJson.initialValue;
              console.log(initialValue);
              console.log(assetId);
              
              handleClose();
            },
          },
        }}
      >
        <DialogTitle>Registrar um investimento</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Para registrar um investimento, por favor, insira o valor inicial do investimento.
          </DialogContentText>
          <TextField
            autoFocus
            required
            margin="dense"
            id="initialValue"
            name="initialValue"
            label="Valor inicial"
            type="number"
            fullWidth
            variant="standard"
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancelar</Button>
          <Button type="submit">Registrar</Button>
        </DialogActions>
      </Dialog>
    </React.Fragment>
  );
}