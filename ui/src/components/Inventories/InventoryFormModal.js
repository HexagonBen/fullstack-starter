import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import Grid from '@material-ui/core/Grid'
import React from 'react'
import TextField from '../Form/TextField'
import { Field, Form, Formik } from 'formik'
import { MeasurementUnits } from "../../constants/units"
import { MenuItem, Checkbox, FormControlLabel } from "@material-ui/core"
import { useSelector } from "react-redux"

class InventoryFormModal extends React.Component {
    render() {
        const {
            formName,
            handleDialog,
            handleInventory,
            title,
            initialValues
        } = this.props

        const validate = values => {
            const errors = {};

            if (!values.name) {
                errors.name = "An inventory name is required."
            }
            if (!values.productType) {
                errors.productType = "A product type must be selected."
            }
            if (values.averagePrice < 0) {
                errors.averagePrice = "Average price cannot be negative."
            }
            if (values.amount < 0) {
                 errors.amount = "Amount cannot be negative."
            }
            if (!values.unitOfMeasurement) {
                errors.unitOfMeasurement = "A unit of measurement must be selected."
            }

            return errors;
        }

        return (
            <Dialog
                open={this.props.isDialogOpen}
                maxWidth='sm'
                fullWidth={true}
                onClose={() => { handleDialog(false) }}
            >
                <Formik
                    initialValues={initialValues}
                    onSubmit={values => {
                        handleInventory(values)
                        handleDialog(true)
                    }}
                    validate = {validate}
			    >
                    {helpers =>
                        <Form
                            autoComplete='off'
                            id={formName}
                        >
                            <DialogTitle id='alert-dialog-title'>
                                {`${title} Inventory`}
                            </DialogTitle>
                            <DialogContent>
                                <Grid container>
                                    {/*Name*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='name'
                                            label='Name'
                                            required
                                            component={TextField}
                                        />
                                    </Grid>
                                    {/*Product Type*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field>
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='productType'
                                            label='Product Type'
                                            required
                                            component={TextField} select
                                            {useSelector(state => state.products.all).map(product => {
                                                return(<MenuItem value={product.name} key={product.id}>{product.name}</MenuItem>)
                                            })}
                                        </Field>
                                    </Grid>
                                    {/*Description*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='description'
                                            label='Description'
                                            component={TextField}
                                            multiline rows={1}
                                        />
                                    </Grid>
                                    {/*Average Price*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='averagePrice'
                                            label='Average Price'
                                            type="number"
                                            component={TextField}
                                        />
                                    </Grid>
                                    {/*Amount*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='amount'
                                            label='Amount'
                                            type="number"
                                            component={TextField}
                                        />
                                    </Grid>
                                    {/*Unit of Measurement*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field>
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='unitOfMeasurement'
                                            label='Unit of Measurement'
                                            required
                                            component={TextField} select
                                            {Object.keys(MeasurementUnits).map(unit => {
                                                return(<MenuItem value={unit} key={MeasurementUnits[unit].name}>{unit}</MenuItem>)
                                            })}
                                        </Field>
                                    </Grid>
                                    {/*Best Before Date*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='bestBeforeDate'
                                            label='Best Before Date'
                                            type="date"
                                            component={TextField}
                                        />
                                    </Grid>
                                    {/*Never Expires*/}
                                    <Grid item xs={12} sm={12}>
                                        <Field
                                            custom={{ variant: 'outlined', fullWidth: true, }}
                                            name='neverExpires'
                                            label='Never Expires'
                                            type="checkbox"
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={() => { handleDialog(false) }} color='secondary'>Cancel</Button>
                                <Button
                                    disableElevation
                                    variant='contained'
                                    type='submit'
                                    form={formName}
                                    color='secondary'
                                    disabled={!helpers.dirty}>
                                    Save
                                </Button>
                            </DialogActions>
                        </Form>
                    }
                </Formik>
            </Dialog>
        )
    }
}

export default InventoryFormModal

